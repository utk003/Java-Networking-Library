package me.utk.networking;

import me.utk.networking.nio.NewIO_Client;
import me.utk.networking.oio.OldIO_Client;

import java.util.function.Supplier;

/**
 * A static wrapper for a singular {@link ClientImplementation} instance.
 * <p>
 * This class provides a single static instance of a {@code ClientImplementation}, which can be
 * instantiated via the {@link #initialize(boolean)} and {@link #initialize(Supplier)} methods.
 * <p>
 * Additionally, all of the methods implemented by a {@code ClientImplementation} can be
 * accessed via the corresponding static methods offered by this class.
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see #initialize(boolean)
 * @see #initialize(Supplier)
 * @see ClientImplementation
 */
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
     * Resolving the old client implementation via the {@link ClientImplementation#closeClient()}
     * method is left as a task for the user.
     *
     * @param useNewIO Whether to use Java's New or Old IO API
     * @return {@code true}, if the current client was replaced (as outlined in
     * {@link Client#initialize(Supplier)}'s documentation; otherwise, {@code false}
     * @see Client#initialize(Supplier)
     * @see NewIO_Client
     * @see OldIO_Client
     * @see ClientImplementation
     * @see ClientImplementation#closeClient()
     */
    public static boolean initialize(boolean useNewIO) {
        return initialize(() -> useNewIO ? NewIO_Client.defaultImplementation() : OldIO_Client.defaultImplementation());
    }
    /**
     * Initializes this abstract class's {@link ClientImplementation} instance to
     * the provided implementation, provided it is not {@code null}.
     * <p>
     * Resolving the old client implementation via the {@link ClientImplementation#closeClient()}
     * method is left as a task for the user.
     *
     * @param server A supplier which provides a new {@code ClientImplementation}
     * @return {@code true}, if the provided {@code ClientImplementation} is not {@code null}; otherwise, {@code false}
     * @see ClientImplementation
     * @see ClientImplementation#closeClient()
     */
    public static boolean initialize(Supplier<ClientImplementation> server) {
        ClientImplementation imp;
        if (server == null || (imp = server.get()) == null)
            return false;
        instance = imp;
        return true;
    }

    /**
     * Returns whether or not the static {@link ClientImplementation} is instantiated.
     *
     * @return {@code true} if the static {@code ClientImplementation}
     * is not {@code null}; otherwise, {@code false}
     * @see #initialize(boolean)
     * @see #initialize(Supplier)
     * @see ClientImplementation
     */
    public static boolean exists() {
        return instance != null;
    }

    /**
     * Sends the given messages to the server using implementation-specific messaging protocols.
     * <p>
     * This method is a static wrapper for the {@link ClientImplementation#sendMessages(String...)} method.
     *
     * @param messages String messages to send to client
     * @throws NullPointerException if the static {@link ClientImplementation} was not initialized
     * @see ClientImplementation
     * @see ClientImplementation#sendMessages(String...)
     */
    public static void sendMessages(String... messages) {
        instance.sendMessages(messages);
    }
    /**
     * Returns the {@link MessageBuilder} associated with this client.
     * <p>
     * This method is a static wrapper for the {@link ClientImplementation#getMessageBuilder()} method.
     *
     * @return The {@code MessageBuilder} associated with this client
     * @throws NullPointerException if the static {@link ClientImplementation} was not initialized
     * @see ClientImplementation
     * @see ClientImplementation#getMessageBuilder()
     */
    public static MessageBuilder getMessageBuilder() {
        return instance.getMessageBuilder();
    }

    /**
     * Sets the amount of time to wait for server connection before timing out.
     * <p>
     * This method is a static wrapper for the {@link ClientImplementation#setConnectionTimeout(int)} method.
     *
     * @param timeout Number of milliseconds until timeout
     * @throws NullPointerException if the static {@link ClientImplementation} was not initialized
     * @see ClientImplementation
     * @see ClientImplementation#setConnectionTimeout(int)
     */
    public static void setConnectionTimeout(int timeout) {
        instance.setConnectionTimeout(timeout);
    }

    /**
     * Attempts to connect to the specified network address without a passcode.
     * <p>
     * This method is a static wrapper for the {@link ClientImplementation#connect(String, int)} method.
     *
     * @param address The IP address to connect to
     * @param port    The network port to connect to
     * @return {@code true} if the connection was accepted by the server; otherwise, {@code false}
     * @throws NullPointerException if the static {@link ClientImplementation} was not initialized
     * @see ClientImplementation
     * @see ClientImplementation#connect(String, int)
     */
    public static boolean connect(String address, int port) {
        return instance.connect(address, port);
    }
    /**
     * Attempts to connect to the specified network address with the specified passcode.
     * <p>
     * This method is a static wrapper for the {@link ClientImplementation#connect(String, int, String)} method.
     *
     * @param address  The IP address to connect to
     * @param port     The network port to connect to
     * @param passcode The passcode to use to connect
     * @return {@code true} if the connection was accepted by the server; otherwise, {@code false}
     * @throws NullPointerException if the static {@link ClientImplementation} was not initialized
     * @see ClientImplementation
     * @see ClientImplementation#connect(String, int, String)
     */
    public static boolean connect(String address, int port, String passcode) {
        return instance.connect(address, port, passcode);
    }

    /**
     * Closes all threads and resolves all objects in use by this client.
     * <p>
     * This method is a static wrapper for the {@link ClientImplementation#closeClient()} method.
     *
     * @throws NullPointerException if the static {@link ClientImplementation} was not initialized
     * @see ClientImplementation
     * @see ClientImplementation#closeClient()
     */
    public static void closeClient() {
        instance.closeClient();
        instance = null;
    }
}
