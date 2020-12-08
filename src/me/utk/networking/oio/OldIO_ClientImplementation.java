package me.utk.networking.oio;

import me.utk.networking.ClientImplementation;
import me.utk.util.misc.ThreadUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class OldIO_ClientImplementation extends ClientImplementation {
    // ---------------------------------------- PRIVATE INSTANCE VARIABLES ---------------------------------------- //

    private Socket client = null;
    private OldIO_MessageBuilder builder = null;
    private final ThreadUtil.ThreadLocker CLIENT_IMPLEMENTATION_LOCKER = new ThreadUtil.ThreadLocker();

    private boolean isVerifyingConnection = false;
    private final ScheduledExecutorService CONNECTION_VERIFICATION_SERVICE;
    private int timeoutCounter;

    private final ScheduledExecutorService SERVER_MESSAGE_COLLECTION_SERVICE;

    // ---------------------------------------- PACKAGE-PRIVATE CONSTRUCTOR ---------------------------------------- //

    OldIO_ClientImplementation() {
        CONNECTION_VERIFICATION_SERVICE = Executors.newSingleThreadScheduledExecutor();
        CONNECTION_VERIFICATION_SERVICE.scheduleAtFixedRate(
                () -> {
                    if (!isVerifyingConnection)
                        synchronized (CONNECTION_VERIFICATION_SERVICE) {
                            try {
                                CONNECTION_VERIFICATION_SERVICE.wait();
                            } catch (InterruptedException ignored) {
                            }
                        }

                    if (checkConnectionConfirmation()) {
                        isVerifyingConnection = false;
                        synchronized (CLIENT_IMPLEMENTATION_LOCKER) {
                            CLIENT_IMPLEMENTATION_LOCKER.notifyAll();
                        }
                    } else if (++timeoutCounter >= 300) { // 5 mins
                        closeClient();
                        isVerifyingConnection = false;
                        synchronized (CLIENT_IMPLEMENTATION_LOCKER) {
                            CLIENT_IMPLEMENTATION_LOCKER.notifyAll();
                        }
                    }
                },
                0L,
                1000L,
                TimeUnit.MILLISECONDS
        );

        SERVER_MESSAGE_COLLECTION_SERVICE = Executors.newSingleThreadScheduledExecutor();
        SERVER_MESSAGE_COLLECTION_SERVICE.scheduleAtFixedRate(
                () -> {
                    if (client != null) {
                        timeoutCounter = OldIO_Util.readMessages(client, builder, timeoutCounter);
                        if (timeoutCounter > 300)
                            closeClient();
                    }
                },
                0L,
                1000L, // check once every 1s
                TimeUnit.MILLISECONDS
        );
    }

    // ---------------------------------------- PRIVATE HELPER METHODS ---------------------------------------- //

    private boolean checkConnectionConfirmation() {
        return client != null && OldIO_MessageUtil.CONNECTION_CONFIRMATION.toString().equals(OldIO_Util.readLine(client));
    }

    private void closeClient() {
        if (client != null) {
            OldIO_Util.closeSocketUntilSuccess(client);
            client = null;
            builder = null;
        }
    }

    // ---------------------------------------- OVERRIDDEN METHODS ---------------------------------------- //

    @Override
    public void sendMessages(String... messages) {
        if (client != null)
            OldIO_Util.sendMessages(client, messages);
    }
    @Override
    public OldIO_MessageBuilder getMessageBuilder() {
        return builder;
    }

    @Override
    public boolean connect(String addressAndPort) {
        return connect(addressAndPort, "");
    }
    @Override
    public boolean connect(String addressAndPort, String passcode) {
        String[] split = addressAndPort.split(":");
        if (split.length != 2)
            throw new IllegalArgumentException(addressAndPort + " is not a valid network address/port pair");

        int port;
        try {
            port = Integer.parseInt(split[1]);
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException(split[1] + " is not a valid network port");
        }
        return connect(split[0], port, passcode);
    }
    @Override
    public boolean connect(String address, int port) {
        return connect(address, port, "");
    }
    @Override
    public boolean connect(String address, int port, String passcode) {
        if (client != null) {
            sendMessages("" + OldIO_MessageUtil.CONNECTION_CLOSED);
            closeClient();
        }
        try {
            client = new Socket(address, port);
            client.setSoTimeout(100);
        } catch (IOException e) {
            client = null;
            throw new IllegalStateException("Unable to connect to " + address + ":" + port);
        }
        sendMessages(passcode);

        timeoutCounter = 0;
        isVerifyingConnection = true;
        synchronized (CONNECTION_VERIFICATION_SERVICE) {
            CONNECTION_VERIFICATION_SERVICE.notifyAll();
        }
        synchronized (CLIENT_IMPLEMENTATION_LOCKER) {
            try {
                CLIENT_IMPLEMENTATION_LOCKER.wait();
            } catch (InterruptedException ignored) {
            }
        }

        if (client == null)
            return false;

        builder = new OldIO_MessageBuilder();
        timeoutCounter = 0;
        return true;
    }

    @Override
    public void killClient() {
        CONNECTION_VERIFICATION_SERVICE.shutdownNow();
        SERVER_MESSAGE_COLLECTION_SERVICE.shutdownNow();
        sendMessages("" + OldIO_MessageUtil.CONNECTION_CLOSED);
        closeClient();
    }
}
