package me.utk.networking;

import me.utk.networking.nio.NewIO_Server;
import me.utk.networking.oio.OldIO_Server;

import java.util.Set;
import java.util.function.Supplier;

/**
 *
 */
public abstract class Server {
    private Server() {
    }

    private static ServerImplementation instance = null;

    /**
     * Initializes this abstract class's {@link ServerImplementation} instance to a default
     * implementation using either Java's New or Old IO API.
     * <p>
     * This method initializes the server using a default NIO or OIO implementation
     * (from {@link OldIO_Server} or {@link NewIO_Server}) with a randomly generated
     * port allocation.
     * <p>
     * Resolving the old server implementation via the {@link ServerImplementation#killServer()}
     * method is left as a task for the user.
     *
     * @param useNewIO Whether to use Java's New or Old IO API
     * @return {@code true}, if the current server was replaced (as outlined in
     * {@link Server#initialize(Supplier)}'s documentation; otherwise, {@code false}
     * @see Server#initialize(Supplier)
     * @see NewIO_Server
     * @see OldIO_Server
     * @see ServerImplementation
     * @see ServerImplementation#killServer()
     */
    public static boolean initialize(boolean useNewIO) {
        return initialize(() -> useNewIO ? NewIO_Server.defaultImplementation() : OldIO_Server.defaultImplementation());
    }
    /**
     * Initializes this abstract class's {@link ServerImplementation} instance to a default
     * implementation using either Java's New or Old IO API.
     * <p>
     * This method initializes the server using a default NIO or OIO implementation
     * (from {@link OldIO_Server} or {@link NewIO_Server}) with the specified networking port.
     * <p>
     * Resolving the old server implementation via the {@link ServerImplementation#killServer()}
     * method is left as a task for the user.
     *
     * @param useNewIO Whether to use Java's New or Old IO API
     * @param port     The port to use for creating the {@code ServerImplementation}
     * @return {@code true}, if the current server was replaced (as outlined in
     * {@link Server#initialize(Supplier)}'s documentation; otherwise, {@code false}
     * @see Server#initialize(Supplier)
     * @see NewIO_Server
     * @see OldIO_Server
     * @see ServerImplementation
     * @see ServerImplementation#killServer()
     */
    public static boolean initialize(boolean useNewIO, int port) {
        return initialize(() -> useNewIO ? NewIO_Server.defaultImplementation(port) : OldIO_Server.defaultImplementation(port));
    }
    /**
     * Initializes this abstract class's {@link ServerImplementation} instance to
     * the provided implementation, provided it is not {@code null}.
     * <p>
     * Resolving the old server implementation via the {@link ServerImplementation#killServer()}
     * method is left as a task for the user.
     *
     * @param server A supplier which provides a new {@code ServerImplementation}
     * @return {@code true}, if the provided {@code ServerImplementation} is not {@code null}; otherwise, {@code false}
     * @see ServerImplementation
     * @see ServerImplementation#killServer()
     */
    public static boolean initialize(Supplier<ServerImplementation> server) {
        ServerImplementation imp;
        if (server == null || (imp = server.get()) == null)
            return false;
        instance = imp;
        return true;
    }

    public static boolean exists() {
        return instance != null;
    }

    /**
     * Returns the information necessary for connecting clients to this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#getAddress()} method.
     *
     * @return This {@link ServerImplementation}'s corresponding {@link NetworkAddress}
     * @throws NullPointerException if the static {@code ServerImplementation} was not initialized
     * @see NetworkAddress
     * @see ServerImplementation
     * @see ServerImplementation#getAddress()
     */
    public static NetworkAddress getAddress() {
        return instance.getAddress();
    }

    public static Set<ServerSideClient> getClients() {
        return instance.getClients();
    }

    public static String enablePasscode() {
        return instance.enablePasscode();
    }
    public static String enablePasscode(int length) {
        return instance.enablePasscode(length);
    }
    public static String enablePasscode(String code) {
        return instance.enablePasscode(code);
    }
    public static void disablePasscode() {
        instance.disablePasscode();
    }

    public static void enableNewConnections() {
        instance.enableNewConnections();
    }
    public static void disableNewConnections() {
        instance.disableNewConnections();
    }

    public static void enableAllConnections() {
        instance.enableAllConnections();
    }
    public static void disableAllConnections() {
        instance.disableAllConnections();
    }

    public static void closeAllConnections() {
        instance.closeAllConnections();
    }
    public static void killServer() {
        instance.killServer();
        instance = null;
    }
}
