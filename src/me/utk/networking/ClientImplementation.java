package me.utk.networking;

/**
 * The framework for any client implementation in this API.
 * <p>
 * Any {@code ClientImplementation}s must implement the following methods:
 * <ul>
 * <li>{@link #sendMessages(String...)} sends the specified messages to the server
 * <li>{@link #getMessageBuilder()} returns the {@link MessageBuilder} corresponding to this connection
 * <li>{@link #setConnectionTimeout(int)} sets the timeout duration of this client
 * <li>{@link #connect(String, int)} )} tries to connect this client to the specified network address
 * <li>{@link #connect(String, int, String)} tries to connect this client to the specified network address
 * </ul>
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see MessageBuilder
 */
public interface ClientImplementation {
    /**
     * Sends the given messages to the server using implementation-specific messaging protocols.
     * <p>
     * The sent messages are guaranteed to be received in exactly the same order
     * as they were sent, unless specified otherwise in documentation.
     *
     * @param messages String messages to send to client
     * @see MessageBuilder#nextMessage()
     */
    void sendMessages(String... messages);
    /**
     * Returns the {@link MessageBuilder} associated with this client.
     * <p>
     * This {@code MessageBuilder} is responsible for collecting and
     * coalescing received message fragments sent by the server
     * into a complete message that can then be used by this client.
     *
     * @return The {@code MessageBuilder} associated with this client
     * @see MessageBuilder
     */
    MessageBuilder getMessageBuilder();

    /**
     * Sets the amount of time to wait for server connection before timing out.
     * <p>
     * A non-positive input signifies the implementation-specific default timeout duration.
     *
     * @param timeout Number of milliseconds until timeout
     */
    void setConnectionTimeout(int timeout);

    /**
     * Attempts to connect to the specified network address without a passcode.
     *
     * @param address The IP address to connect to
     * @param port    The network port to connect to
     * @return {@code true} if the connection was accepted by the server; otherwise, {@code false}
     */
    boolean connect(String address, int port);
    /**
     * Attempts to connect to the specified network address with the specified passcode.
     *
     * @param address  The IP address to connect to
     * @param port     The network port to connect to
     * @param passcode The passcode to use to connect
     * @return {@code true} if the connection was accepted by the server; otherwise, {@code false}
     */
    boolean connect(String address, int port, String passcode);

    /**
     * Closes all threads and resolves all objects in use by this client.
     * <p>
     * This utility method provides a way of closing threads and resolving any objects in use
     * by this client that could become orphaned when the client is closed.
     * <p>
     * Any users should note that failing to call this method prior to destroying a client
     * (via Java's Garbage Collection) can result in a build-up of inaccessible runaway threads
     * which can consume processing power and even leave the Java Runtime Environment unable to close.
     */
    void closeClient();
}
