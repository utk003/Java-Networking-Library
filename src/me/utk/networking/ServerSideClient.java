package me.utk.networking;

/**
 * A client for a server-side application.
 * <p>
 * This class provides utility methods for sending messages to the client
 * and receiving messages from the client (via a {@link MessageBuilder}).
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see MessageBuilder
 */
public interface ServerSideClient {
    /**
     * Sends the given messages to the specified client,
     * using implementation-specific messaging protocols.
     * <p>
     * The sent messages are guaranteed to be received in
     * exactly the same order as they were sent, unless
     * specified otherwise in documentation.
     *
     * @param messages String messages to send to client
     * @see MessageBuilder#nextMessage()
     */
    void sendMessages(String... messages);
    /**
     * Returns the {@link MessageBuilder} associated with this client.
     * <p>
     * This {@code MessageBuilder} is responsible for collecting and
     * coalescing received message fragments sent by that client
     * into a complete message that can then be used by server.
     *
     * @return The {@code MessageBuilder} associated with this client
     * @see MessageBuilder
     */
    MessageBuilder getMessageBuilder();
}
