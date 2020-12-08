package me.utk.networking;

import me.utk.networking.nio.NewIO_Client;
import me.utk.networking.oio.OldIO_Client;

import java.util.function.Supplier;

public abstract class Client {
    private Client() {
    }

    private static ClientImplementation instance = null;

    /**
     * Initializes this abstract class's {@link ClientImplementation} instance to a default
     * implementation using either Java's New or Old IO API.
     * <p>
     * This method initializes the client using a default NIO or OIO implementation
     * (from {@link NewIO_Client} or {@link OldIO_Client}).
     * <p>
     * Resolving the old client implementation via the {@link ClientImplementation#killClient()}
     * method is left as a task for the user.
     *
     * @param useNewIO Whether to use Java's New or Old IO API
     * @return {@code true}, if the current client was replaced (as outlined in
     * {@link Client#initialize(Supplier)}'s documentation; otherwise, {@code false}
     * @see Client#initialize(Supplier)
     * @see NewIO_Client
     * @see OldIO_Client
     * @see ClientImplementation
     * @see ClientImplementation#killClient()
     */
    public static boolean initialize(boolean useNewIO) {
        return initialize(() -> useNewIO ? NewIO_Client.defaultImplementation() : OldIO_Client.defaultImplementation());
    }
    /**
     * Initializes this abstract class's {@link ClientImplementation} instance to
     * the provided implementation, provided it is not {@code null}.
     * <p>
     * Resolving the old client implementation via the {@link ClientImplementation#killClient()}
     * method is left as a task for the user.
     *
     * @param server A supplier which provides a new {@code ClientImplementation}
     * @return {@code true}, if the provided {@code ClientImplementation} is not {@code null}; otherwise, {@code false}
     * @see ClientImplementation
     * @see ClientImplementation#killClient()
     */
    public static boolean initialize(Supplier<ClientImplementation> server) {
        ClientImplementation imp;
        if (server == null || (imp = server.get()) == null)
            return false;
        instance = imp;
        return true;
    }

    public static boolean exists() {
        return instance != null;
    }

    public static void sendMessages(String... messages) {
        instance.sendMessages(messages);
    }
    public static MessageBuilder getMessageBuilder() {
        return instance.getMessageBuilder();
    }

    public static boolean connect(String addressAndPort) {
        return instance.connect(addressAndPort);
    }
    public static boolean connect(String addressAndPort, String passcode) {
        return instance.connect(addressAndPort, passcode);
    }
    public static boolean connect(String address, int port) {
        return instance.connect(address, port);
    }
    public static boolean connect(String address, int port, String passcode) {
        return instance.connect(address, port, passcode);
    }

    public static void killClient() {
        instance.killClient();
        instance = null;
    }

}
