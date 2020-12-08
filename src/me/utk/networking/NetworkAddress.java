package me.utk.networking;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 *
 */
public abstract class NetworkAddress {
    private final InetAddress ADDRESS;
    private final int PORT;

    /**
     * Constructs a new {@code NetworkAddress} corresponding to the specified {@link ServerSocket}.
     * <p>
     * The default implementation prioritizes {@link InetAddress#getLocalHost()} over
     * {@link ServerSocket#getInetAddress()} for defining the server's IP address. More
     * details can be found with the {@link #getAddress()} method.
     *
     * @param serverSocket The {@code ServerSocket} whose IP address to identify
     * @see #getAddress()
     * @see InetAddress#getLocalHost()
     * @see ServerSocket
     * @see ServerSocket#getInetAddress()
     */
    public NetworkAddress(ServerSocket serverSocket) {
        InetAddress temp;
        try {
            temp = InetAddress.getLocalHost();
        } catch (
                UnknownHostException ignored) {
            temp = serverSocket.getInetAddress();
        }
        ADDRESS = temp;
        PORT = serverSocket.getLocalPort();
    }

    /**
     * Returns the IP address of this {@code NetworkAddress}.
     * <p>
     * Note that {@link ServerSocket#getInetAddress()} does not necessarily return
     * a useful address, typically returning {@code 0.0.0.0} or the IPv6 equivalent
     * instead. This API recommends but does not require using the result of
     * {@link InetAddress#getLocalHost()} or another similar method.
     *
     * @return A usable form of the IP address of this {@code NetworkAddress}
     * @see #NetworkAddress(ServerSocket)
     * @see InetAddress#getLocalHost()
     * @see ServerSocket#getInetAddress()
     */
    public InetAddress getAddress() {
        return ADDRESS;
    }
    /**
     * Returns the port of this {@code NetworkAddress}.
     * <p>
     * This value is guaranteed to return a value between {@code 0} and {@code 65535}, inclusive.
     * Any implementation which does not satisfy this requirement should note that in its documentation.
     *
     * @return The connected socket of this {@code NetworkAddress}
     */
    public int getPort() {
        return PORT;
    }

    @Override
    public String toString() {
        return ADDRESS + ":" + PORT;
    }
}
