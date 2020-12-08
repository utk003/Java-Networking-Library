package me.utk.networking;

import java.net.Socket;

/**
 *
 */
public abstract class ServerSideClient {
    /**
     * Returns the {@link Socket} being used to communicate with a specific client.
     *
     * @return The {@code Socket} connection to the client represented by this {@code ServerSideClient}
     * @see Socket
     */
    public abstract Socket getSocket();

    /**
     * TODO finish this javadoc
     * Sends the given messages sequentially to the specified client.
     *
     * @param messages String messages to send to client
     * @see ServerSideClient
     */
    public abstract void sendMessages(String... messages);
    public abstract MessageBuilder getMessageBuilder();
}
