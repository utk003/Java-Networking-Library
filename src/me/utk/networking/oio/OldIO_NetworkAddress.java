package me.utk.networking.oio;

import me.utk.networking.NetworkAddress;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

// @inheritDoc
class OldIO_NetworkAddress implements NetworkAddress {
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
    OldIO_NetworkAddress(ServerSocket serverSocket) {
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

    @Override // @inheritDoc
    public InetAddress getAddress() {
        return ADDRESS;
    }

    @Override // @inheritDoc
    public int getPort() {
        return PORT;
    }

    @Override // @inheritDoc
    public String toString() {
        return ADDRESS + ":" + PORT;
    }
}
