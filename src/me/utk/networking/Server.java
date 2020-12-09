package me.utk.networking;

import me.utk.networking.nio.NewIO_Server;
import me.utk.networking.oio.OldIO_Server;

import java.util.Set;
import java.util.function.Supplier;

/**
 * A static wrapper for a singular {@link ServerImplementation} instance.
 * <p>
 * This class provides a single static instance of a {@code ServerImplementation},
 * which can be instantiated via the {@link #initialize(boolean)}, {@link #initialize(boolean, int)},
 * and {@link #initialize(Supplier)} methods.
 * <p>
 * Additionally, all of the methods implemented by a {@code ServerImplementation} can be
 * accessed via the corresponding static methods offered by this class.
 * 
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see #initialize(boolean)
 * @see #initialize(boolean, int)
 * @see #initialize(Supplier)
 * @see ServerImplementation
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
     * Resolving the old server implementation via the {@link ServerImplementation#closeServer()}
     * method is left as a task for the user.
     *
     * @param useNewIO Whether to use Java's New or Old IO API
     * @return {@code true}, if the current server was replaced (as outlined in
     * {@link Server#initialize(Supplier)}'s documentation; otherwise, {@code false}
     * @see Server#initialize(Supplier)
     * @see NewIO_Server
     * @see OldIO_Server
     * @see ServerImplementation
     * @see ServerImplementation#closeServer()
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
     * Resolving the old server implementation via the {@link ServerImplementation#closeServer()}
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
     * @see ServerImplementation#closeServer()
     */
    public static boolean initialize(boolean useNewIO, int port) {
        return initialize(() -> useNewIO ? NewIO_Server.defaultImplementation(port) : OldIO_Server.defaultImplementation(port));
    }
    /**
     * Initializes this abstract class's {@link ServerImplementation} instance to
     * the provided implementation, provided it is not {@code null}.
     * <p>
     * Resolving the old server implementation via the {@link ServerImplementation#closeServer()}
     * method is left as a task for the user.
     *
     * @param server A supplier which provides a new {@code ServerImplementation}
     * @return {@code true}, if the provided {@code ServerImplementation} is not {@code null}; otherwise, {@code false}
     * @see ServerImplementation
     * @see ServerImplementation#closeServer()
     */
    public static boolean initialize(Supplier<ServerImplementation> server) {
        ServerImplementation imp;
        if (server == null || (imp = server.get()) == null)
            return false;
        instance = imp;
        return true;
    }

    /**
     * Returns whether or not the static {@link ServerImplementation} is instantiated.
     *
     * @return {@code true} if the static {@code ServerImplementation}
     * is not {@code null}; otherwise, {@code false}
     * @see #initialize(boolean)
     * @see #initialize(boolean, int)
     * @see #initialize(Supplier)
     * @see ServerImplementation
     */
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

    /**
     * Returns a set of {@link ServerSideClient}s corresponding to all clients
     * which have successfully connected and been verified by this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#getClients()} method.
     *
     * @return A set of {@code ServerSideClient}s corresponding to this server's clients
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#getClients()
     * @see ServerSideClient
     */
    public static Set<ServerSideClient> getClients() {
        return instance.getClients();
    }

    /**
     * Enables a randomly generated alpha-numeric passcode
     * with an implementation-specific default length.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#enablePasscode()} method.
     *
     * @return The new passcode
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#enablePasscode()
     */
    public static String enablePasscode() {
        return instance.enablePasscode();
    }
    /**
     * Enables a randomly generated alpha-numeric passcode with the specified length.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#enablePasscode(int)} method.
     *
     * @param length The length of the new passcode
     * @return The new passcode
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#enablePasscode(int)
     */
    public static String enablePasscode(int length) {
        return instance.enablePasscode(length);
    }
    /**
     * Sets the given code as the new server passcode.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#enablePasscode(String)} method.
     *
     * @param code The new passcode for this server
     * @return The new passcode
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#enablePasscode(String)
     */
    public static String enablePasscode(String code) {
        return instance.enablePasscode(code);
    }
    /**
     * Disables this server passcode entirely.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#disablePasscode()} method.
     *
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#disablePasscode()
     */
    public static void disablePasscode() {
        instance.disablePasscode();
    }

    /**
     * Permits new clients to attempt to connect to this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#enableNewConnections()} method.
     *
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#enableNewConnections()
     */
    public static void enableNewConnections() {
        instance.enableNewConnections();
    }
    /**
     * Prohibits new clients to attempt to connect to this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#disableNewConnections()} method.
     *
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#disableNewConnections()
     */
    public static void disableNewConnections() {
        instance.disableNewConnections();
    }

    /**
     * Permits all clients to connect to this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#enableAllConnections()} method.
     *
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#enableAllConnections()
     */
    public static void enableAllConnections() {
        instance.enableAllConnections();
    }
    /**
     * Prohibits all clients to connect to this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#disableAllConnections()} method.
     *
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#disableAllConnections()
     */
    public static void disableAllConnections() {
        instance.disableAllConnections();
    }

    /**
     * Closes all currently active or open connections to this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#closeAllConnections()} method.
     *
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#closeAllConnections()
     */
    public static void closeAllConnections() {
        instance.closeAllConnections();
    }
    /**
     * Closes all threads and resolves all objects in use by this server.
     * <p>
     * This method is a static wrapper for the {@link ServerImplementation#closeServer()} method.
     *
     * @throws NullPointerException if the static {@link ServerImplementation} was not initialized
     * @see ServerImplementation
     * @see ServerImplementation#closeServer()
     */
    public static void closeServer() {
        instance.closeServer();
        instance = null;
    }
}
