package me.utk.networking;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * An IP address/port pair which represents a connection address to a server.
 * <p>
 * This interface is designed to complement {@link ServerImplementation}s,
 * and it is not based upon any standard address definitions or conventions.
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see ServerImplementation
 */
public interface NetworkAddress {
    /**
     * Returns the IP address of this {@code NetworkAddress}.
     * <p>
     * Note that {@link ServerSocket#getInetAddress()} does not necessarily return
     * a useful address, typically returning {@code 0.0.0.0} or the IPv6 equivalent
     * instead. This API recommends but does not require using the result of
     * {@link InetAddress#getLocalHost()} or another similar method.
     *
     * @return A usable form of the IP address of this {@code NetworkAddress}
     * @see InetAddress#getLocalHost()
     * @see ServerSocket#getInetAddress()
     */
    InetAddress getAddress();
    /**
     * Returns the port of this {@code NetworkAddress}.
     * <p>
     * This value is guaranteed to return a value between {@code 0} and {@code 65535}, inclusive.
     * Any implementation which does not satisfy this requirement should note that in its documentation.
     *
     * @return The connected socket of this {@code NetworkAddress}
     */
    int getPort();
}
