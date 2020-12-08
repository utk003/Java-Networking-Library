package me.utk.networking.oio;

import me.utk.networking.NetworkAddress;
import me.utk.networking.ServerImplementation;
import me.utk.networking.ServerSideClient;
import me.utk.util.misc.ThreadUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class OldIO_ServerImplementation extends ServerImplementation {
    // ---------------------------------------- PRIVATE INSTANCE VARIABLES ---------------------------------------- //

    private final ServerSocket SERVER_SOCKET;

    private final Semaphore ATTEMPTED_SEMAPHORE, VERIFIED_SEMAPHORE;
    private final Map<Socket, Integer> ATTEMPTING_CONNECTIONS;
    private final Map<OldIO_ServerSideClient, Integer> VERIFIED_CONNECTIONS;

    private boolean isAcceptingNewConnections = false, isAcceptingAnyConnections = false;
    private final ThreadUtil.ThreadLocker CONNECTION_THREAD_LOCK = new ThreadUtil.ThreadLocker(false);
    private boolean connectionThreadTerminationCondition = false;

    private String connectionPasscode = ""; // default is no passcode
    private final ScheduledExecutorService VERIFICATION_SERVICE;

    private final ScheduledExecutorService CLIENT_MESSAGE_COLLECTION_SERVICE;

    private final NetworkAddress ADDRESS;

    // ---------------------------------------- PACKAGE-PRIVATE CONSTRUCTOR ---------------------------------------- //

    OldIO_ServerImplementation(int port) {
        /*
         * Create new ServerSocket during initialization
         */
        try {
            SERVER_SOCKET = new ServerSocket(port);
            SERVER_SOCKET.setSoTimeout(1000); // 1s timeouts for making connections
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Unable to initialize ServerImplementation for Server class");
        }

        /*
         * Creates the attempting/verified connections maps (mapping to timeout counters) as well as Semaphores for those maps
         */
        ATTEMPTED_SEMAPHORE = new Semaphore(1, true);
        ATTEMPTING_CONNECTIONS = new HashMap<>();

        VERIFIED_SEMAPHORE = new Semaphore(1, true);
        VERIFIED_CONNECTIONS = new HashMap<>();

        /*
         * Creates and runs a thread for collecting attempted connections into map along with a
         * ThreadLocker for locking the thread when incoming connections are no longer being accepted.
         */
        new Thread(() -> {
            // Loop until thread is terminated
            while (!connectionThreadTerminationCondition) {
                // Logic to lock thread if new connections are being rejected
                if (!isAcceptingNewConnections)
                    synchronized (CONNECTION_THREAD_LOCK) {
                        try {
                            CONNECTION_THREAD_LOCK.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                // Try to connect a new socket, and put into attempting connections map if successful
                try {
                    Socket socket = SERVER_SOCKET.accept();
                    socket.setSoTimeout(100); // 100ms timeout for socket interactions
                    ATTEMPTED_SEMAPHORE.acquire();
                    ATTEMPTING_CONNECTIONS.put(socket, 0);
                    ATTEMPTED_SEMAPHORE.release();
                } catch (IOException | InterruptedException ignored) {
                }
            }
        }).start();

        /*
         * Create a default (lack of) password and a {@link ScheduledExecutorService} to
         * check if any attempting connections have inputted the correct passcode.
         */
        // Get a new ScheduledExecutorService
        VERIFICATION_SERVICE = Executors.newSingleThreadScheduledExecutor();
        // Schedule verification task to happen once per second
        VERIFICATION_SERVICE.scheduleAtFixedRate(
                // Task
                () -> {
                    // Logic to stop if connections are being rejected
                    if (!isAcceptingAnyConnections)
                        synchronized (VERIFICATION_SERVICE) {
                            try {
                                VERIFICATION_SERVICE.wait();
                            } catch (InterruptedException ignored) {
                            }
                        }
                    // Go through all attempting connections and accept any which put in the right passcode
                    try {
                        // Get semaphore to prevent race conditions
                        ATTEMPTED_SEMAPHORE.acquire();

                        Map.Entry<Socket, Integer> entry;
                        Socket client;
                        Iterator<Map.Entry<Socket, Integer>> it = ATTEMPTING_CONNECTIONS.entrySet().iterator();
                        // Iterate through attempting connections map
                        while (it.hasNext()) {
                            entry = it.next();
                            client = entry.getKey();
                            // If password matches, move to verified connections
                            // Otherwise increment timeout counter (fails after 300 secs = 5 mins)
                            if (verifyPassword(client)) {
                                it.remove();
                                // Send confirmation message to client
                                try {
                                    // Wrap socket in ServerSideClient object and put into verified connections map
                                    OldIO_ServerSideClient ssc = new OldIO_ServerSideClient(client);
                                    VERIFIED_SEMAPHORE.acquire();
                                    VERIFIED_CONNECTIONS.put(ssc, 0);
                                    VERIFIED_SEMAPHORE.release();
                                    ssc.sendMessages("" + OldIO_MessageUtil.CONNECTION_CONFIRMATION);
                                } catch (InterruptedException ignored) {
                                }
                            } else {
                                // Increment timeout count by 1 and close socket if fails > 300 secs = 5 mins
                                int fails = entry.getValue() + 1;
                                if (fails >= 300) { // no correct passcode for 5 mins
                                    it.remove();
                                    OldIO_Util.closeSocketUntilSuccess(client);
                                } else
                                    entry.setValue(fails);
                            }
                        }

                        // Release attempting connections semaphore
                        ATTEMPTED_SEMAPHORE.release();
                    } catch (InterruptedException ignored) {
                    }
                },
                0L,
                1000L, // Check once every 1s = 1000ms
                TimeUnit.MILLISECONDS
        );

        /*
         * Create message collection ScheduledExecutorService
         */
        // Get new ScheduledExecutorService
        CLIENT_MESSAGE_COLLECTION_SERVICE = Executors.newSingleThreadScheduledExecutor();
        // Schedule
        CLIENT_MESSAGE_COLLECTION_SERVICE.scheduleAtFixedRate(
                // Task
                () -> {
                    try {
                        // Get verified connections semaphore to prevent race conditions
                        VERIFIED_SEMAPHORE.acquire();

                        // Iterate through all connections for possible messages
                        Iterator<Map.Entry<OldIO_ServerSideClient, Integer>> it = VERIFIED_CONNECTIONS.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<OldIO_ServerSideClient, Integer> entry = it.next();
                            OldIO_ServerSideClient ssc = entry.getKey();
                            // Outsource reading message logic to Util class
                            int timeoutCounter = OldIO_Util.readMessages(ssc.getSocket(), ssc.getMessageBuilder(), entry.getValue());
                            // Close connection if timed out -> 5 mins = 300 secs
                            if (timeoutCounter > 300) { // 5 mins = 300 secs
                                ssc.sendMessages("" + OldIO_MessageUtil.CONNECTION_CLOSED);
                                it.remove();
                                OldIO_Util.closeSocketUntilSuccess(ssc.getSocket());
                            } else
                                entry.setValue(timeoutCounter);
                        }

                        // Release verified connections semaphore
                        VERIFIED_SEMAPHORE.release();
                    } catch (InterruptedException ignored) {
                    }
                },
                0L,
                1000L, // Check for messages every 1s
                TimeUnit.MILLISECONDS
        );

        /*
         * Create a {@link me.utk.networking.Server.NetworkAddress}
         * for this {@link me.utk.networking.Server.ServerImplementation}
         */
        ADDRESS = new NetworkAddress(SERVER_SOCKET) {
        };
    }

    // ---------------------------------------- PRIVATE HELPER METHODS ---------------------------------------- //

    /**
     * Returns {@code true} iff there is no password or client sent password
     */
    private boolean verifyPassword(Socket client) {
        return connectionPasscode.isEmpty() || connectionPasscode.equalsIgnoreCase(OldIO_Util.readLine(client));
    }

    /**
     * Generate a random alphanumeric string of the given length
     * Capitalization doesn't matter
     */
    private String randomAlphaNumeric(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int charInd = (int) (36 * Math.random());
            if (charInd < 10)
                builder.append(charInd);
            else
                builder.append((char) ('A' + charInd - 10));
        }
        return builder.toString();
    }

    /**
     * Close all connections and give specified reason
     */
    private void closeAllConnections(OldIO_MessageUtil reason) {
        String stringReason = "" + reason;
        // Close all attempting connections
        try {
            ATTEMPTED_SEMAPHORE.acquire();
            for (Socket client : ATTEMPTING_CONNECTIONS.keySet()) {
                OldIO_Util.sendMessages(client, stringReason);
                OldIO_Util.closeSocketUntilSuccess(client);
            }
            ATTEMPTING_CONNECTIONS.clear();
            ATTEMPTED_SEMAPHORE.release();
        } catch (InterruptedException ignored) {
        }
        // close all verified connections
        try {
            VERIFIED_SEMAPHORE.acquire();
            for (OldIO_ServerSideClient ssc : VERIFIED_CONNECTIONS.keySet()) {
                Socket client = ssc.getSocket();
                OldIO_Util.sendMessages(client, stringReason);
                OldIO_Util.closeSocketUntilSuccess(client);
            }
            VERIFIED_CONNECTIONS.clear();
            VERIFIED_SEMAPHORE.release();
        } catch (InterruptedException ignored) {
        }
    }

    // ---------------------------------------- OVERRIDDEN METHODS ---------------------------------------- //

    @Override
    public NetworkAddress getAddress() {
        return ADDRESS;
    }

    @Override
    public Set<ServerSideClient> getClients() {
        Set<ServerSideClient> clients;
        try {
            VERIFIED_SEMAPHORE.acquire();
            clients = new HashSet<>(VERIFIED_CONNECTIONS.keySet());
            VERIFIED_SEMAPHORE.release();
        } catch (InterruptedException ignored) {
            clients = new HashSet<>();
        }
        return clients;
    }

    @Override
    public String enablePasscode() {
        return enablePasscode(randomAlphaNumeric(6));
    }
    @Override
    public String enablePasscode(int length) {
        return enablePasscode(randomAlphaNumeric(length));
    }
    @Override
    public String enablePasscode(String code) {
        return code != null ? connectionPasscode = code : connectionPasscode;
    }
    @Override
    public void disablePasscode() {
        connectionPasscode = "";
    }

    @Override
    public void enableNewConnections() {
        if (isAcceptingAnyConnections) {
            isAcceptingNewConnections = true;
            synchronized (CONNECTION_THREAD_LOCK) {
                CONNECTION_THREAD_LOCK.notifyAll();
            }
        }
    }
    @Override
    public void disableNewConnections() {
        isAcceptingNewConnections = false;
    }

    @Override
    public void enableAllConnections() {
        isAcceptingAnyConnections = true;
        enableNewConnections();
        synchronized (VERIFICATION_SERVICE) {
            VERIFICATION_SERVICE.notifyAll();
        }
    }
    @Override
    public void disableAllConnections() {
        isAcceptingAnyConnections = false;
        disableNewConnections();
    }

    @Override
    public void closeAllConnections() {
        closeAllConnections(OldIO_MessageUtil.CONNECTION_CLOSED);
    }
    @Override
    public void killServer() {
        closeAllConnections(OldIO_MessageUtil.SERVER_CLOSED);
        connectionThreadTerminationCondition = true;
        VERIFICATION_SERVICE.shutdownNow();
        CLIENT_MESSAGE_COLLECTION_SERVICE.shutdownNow();
    }
}
