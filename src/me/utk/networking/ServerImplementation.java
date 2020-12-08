package me.utk.networking;

import java.util.Set;

/**
 *
 */
public abstract class ServerImplementation {
    /**
     * Returns the information necessary for connecting clients to this server.
     * <p>
     * This method returns a {@link NetworkAddress} object which contains both the
     * IP address and the port number corresponding to this {@code ServerImplementation}.
     *
     * @return This {@code ServerImplementation}'s corresponding {@code NetworkAddress}
     * @see NetworkAddress
     */
    public abstract NetworkAddress getAddress();

    /**
     * Returns a set of {@link ServerSideClient}s corresponding to all clients
     * which have successfully connected and been verified by the server.
     * <p>
     * The returned set is not guaranteed to dynamically update to reflect changes
     * in server connections. While some implementations may provide this feature,
     * the only way to ensure an up-to-date set is to call this method again.
     * Additionally, changes to the returned set may not necessarily be reflected
     * in the server.
     *
     * @return A set of {@code ServerSideClient}s corresponding to this server's clients
     * @see ServerSideClient
     */
    public abstract Set<ServerSideClient> getClients();

    public abstract String enablePasscode();
    public abstract String enablePasscode(int length);
    public abstract String enablePasscode(String code);
    public abstract void disablePasscode();

    public abstract void enableNewConnections();
    public abstract void disableNewConnections();

    public abstract void enableAllConnections();
    public abstract void disableAllConnections();

    public abstract void closeAllConnections();
    public abstract void killServer();
}
