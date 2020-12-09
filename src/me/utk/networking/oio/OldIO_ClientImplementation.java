package me.utk.networking.oio;

import me.utk.networking.ClientImplementation;
import me.utk.util.misc.ThreadUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class OldIO_ClientImplementation implements ClientImplementation {
    // ---------------------------------------- PRIVATE INSTANCE VARIABLES ---------------------------------------- //

    private Socket client = null;
    private OldIO_MessageBuilder builder = null;
    private final ThreadUtil.ThreadLocker CLIENT_IMPLEMENTATION_LOCKER = new ThreadUtil.ThreadLocker();

    private boolean isVerifyingConnection = false;
    private final ScheduledExecutorService CONNECTION_VERIFICATION_SERVICE;
    private int timeoutCounter;

    private final ScheduledExecutorService SERVER_MESSAGE_COLLECTION_SERVICE;

    private int connectionTimeoutSeconds;

    // ---------------------------------------- PACKAGE-PRIVATE CONSTRUCTOR ---------------------------------------- //

    OldIO_ClientImplementation() {
        setConnectionTimeout(0);
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
                    } else if (++timeoutCounter >= connectionTimeoutSeconds) { // default value -> 300 secs = 5 mins
                        closeSocket();
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
                            closeSocket();
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

    private void closeSocket() {
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
    public void setConnectionTimeout(int timeout) {
        connectionTimeoutSeconds = timeout <= 0 ? 300 : timeout / 1000 + 1;
    }

    @Override
    public boolean connect(String address, int port) {
        return connect(address, port, "");
    }
    @Override
    public boolean connect(String address, int port, String passcode) {
        if (client != null) {
            sendMessages("" + OldIO_MessageUtil.CONNECTION_CLOSED);
            closeSocket();
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
    public void closeClient() {
        CONNECTION_VERIFICATION_SERVICE.shutdownNow();
        SERVER_MESSAGE_COLLECTION_SERVICE.shutdownNow();
        sendMessages("" + OldIO_MessageUtil.CONNECTION_CLOSED);
        closeSocket();
    }
}
