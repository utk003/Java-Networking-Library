package me.utk.networking;

import java.util.Set;

/**
 * The framework for any server implementation in this API.
 * <p>
 * Any {@code ServerImplementation}s must implement the following methods:
 * <ul>
 * <li>{@link #getAddress()} returns a {@link NetworkAddress} corresponding to this server
 * <li>{@link #getClients()} returns a set of all clients connected to this server
 * <li>{@link #enablePasscode()} enables a randomly generated passcode to connect to this server
 * <li>{@link #enablePasscode(int)} )} enables a randomly generated passcode to connect to this server
 * <li>{@link #enablePasscode(String)} )} enables the specified passcode to connect to this server
 * <li>{@link #disablePasscode()} disables any passcode to connect to this server
 * <li>{@link #enableNewConnections()} enables new connections to this server
 * <li>{@link #disableNewConnections()} disables new connections to this server
 * <li>{@link #enableAllConnections()} enables all connections to this server
 * <li>{@link #disableAllConnections()} disables all connections to this server
 * <li>{@link #closeAllConnections()} closes all currently open connections to this server
 * <li>{@link #closeServer()} completely shuts down this server
 * </ul>
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see NetworkAddress
 */
public interface ServerImplementation {
    /**
     * Returns the information necessary for connecting clients to this server.
     * <p>
     * This method returns a {@link NetworkAddress} object which contains both the
     * IP address and the port number corresponding to this {@code ServerImplementation}.
     *
     * @return This {@code ServerImplementation}'s corresponding {@code NetworkAddress}
     * @see NetworkAddress
     */
    NetworkAddress getAddress();

    /**
     * Returns a set of {@link ServerSideClient}s corresponding to all clients
     * which have successfully connected and been verified by this server.
     * <p>
     * The returned set is not guaranteed to dynamically update to reflect changes
     * in server connections. While some implementations may provide this feature,
     * the only way to ensure an up-to-date set is to call this method again.
     * Additionally, changes to the returned set may not necessarily be reflected
     * in this server.
     *
     * @return A set of {@code ServerSideClient}s corresponding to this server's clients
     * @see ServerSideClient
     */
    Set<ServerSideClient> getClients();

    /**
     * Enables a randomly generated alpha-numeric passcode
     * with an implementation-specific default length.
     * <p>
     * The passcode can be disabled using the {@link #disablePasscode()} method.
     *
     * @return The new passcode
     * @see #enablePasscode(int)
     * @see #enablePasscode(String)
     * @see #disablePasscode()
     */
    String enablePasscode();
    /**
     * Enables a randomly generated alpha-numeric passcode with the specified length.
     * <p>
     * The passcode can be disabled using the {@link #disablePasscode()} method.
     *
     * @param length The length of the new passcode
     * @return The new passcode
     * @see #enablePasscode()
     * @see #enablePasscode(String)
     * @see #disablePasscode()
     */
    String enablePasscode(int length);
    /**
     * Sets the given code as the new server passcode.
     * <p>
     * The passcode can be disabled using the {@link #disablePasscode()} method.
     *
     * @param code The new passcode for this server
     * @return The new passcode
     * @see #enablePasscode()
     * @see #enablePasscode(int)
     * @see #disablePasscode()
     */
    String enablePasscode(String code);
    /**
     * Disables this server passcode entirely.
     * <p>
     * The server passcode can always be re-enabled using the {@link #enablePasscode()},
     * {@link #enablePasscode(int)}, and {@link #enablePasscode(String)} methods.
     *
     * @see #enablePasscode()
     * @see #enablePasscode(int)
     * @see #enablePasscode(String)
     */
    void disablePasscode();

    /**
     * Permits new clients to attempt to connect to this server.
     * <p>
     * If new connections are enabled, clients will be able to
     * connect to this server. Otherwise, incoming connections will
     * be rejected or ignored, depending on the specific implementation.
     * <p>
     * On the other hand, pending connections, specifically relating to
     * connections which have already been established but are waiting for
     * passcode verification will still be accepted and verified.
     *
     * @see #disableNewConnections()
     */
    void enableNewConnections();
    /**
     * Prohibits new clients to attempt to connect to this server.
     * <p>
     * If new connections are enabled, clients will be able to
     * connect to this server. Otherwise, incoming connections will
     * be rejected or ignored, depending on the specific implementation.
     * <p>
     * On the other hand, pending connections, specifically relating to
     * connections which have already been established but are waiting for
     * passcode verification will still be accepted and verified.
     *
     * @see #enableNewConnections()
     */
    void disableNewConnections();

    /**
     * Permits all clients to connect to this server.
     * <p>
     * If connections are enabled, new clients will be able to
     * connect to this server. Otherwise, incoming connections will
     * be rejected or ignored, depending on the specific implementation.
     * <p>
     * Additionally, pending connections, specifically relating to
     * connections which have already been established but are waiting for
     * passcode verification will also be accepted and verified.
     *
     * @see #disableAllConnections()
     */
    void enableAllConnections();
    /**
     * Prohibits all clients to connect to this server.
     * <p>
     * If connections are enabled, new clients will be able to
     * connect to this server. Otherwise, incoming connections will
     * be rejected or ignored, depending on the specific implementation.
     * <p>
     * Additionally, pending connections, specifically relating to
     * connections which have already been established but are waiting for
     * passcode verification will also be accepted and verified.
     *
     * @see #enableAllConnections()
     */
    void disableAllConnections();

    /**
     * Closes all currently active or open connections to this server.
     * <p>
     * This method closes all connections, both ones which have been verified
     * and ones which are pending passcode verification. All clients should be
     * appropriately notified (according to the implementation-specific protocol)
     * that the connection has been severed.
     */
    void closeAllConnections();
    /**
     * Closes all threads and resolves all objects in use by this server.
     * <p>
     * This utility method provides a way of closing threads and resolving any objects in use
     * by this server that could become orphaned when the server is closed.
     * <p>
     * Any users should note that failing to call this method prior to destroying a server
     * (via Java's Garbage Collection) can result in a build-up of inaccessible runaway threads
     * which can consume processing power and even leave the Java Runtime Environment unable to close.
     */
    void closeServer();
}
