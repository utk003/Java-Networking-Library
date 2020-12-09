package me.utk.networking;

/**
 * A message builder which constructs messages from fragments sent over a network.
 * <p>
 * Specific implementations can utilize custom markers, flags, or
 * keywords to specify commands. There are no API requirements on this
 * class other than the conditions specified in the method documentation.
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 */
public interface MessageBuilder {
    /**
     * Returns {@code true} iff the {@link #nextMessage()} method will be able to
     * return a complete message on its next call.
     * <p>
     * A message is considered complete when all of the information sent as a part of the
     * message is received and coalesced into a single {@code String[]}.
     *
     * @return {@code true} if a call to the {@code nextMessage} method will be able
     * to successfully return a complete message; otherwise, {@code false}
     * @see #nextMessage()
     */
    boolean hasMoreMessages();
    /**
     * Returns a not-{@code null String[]} containing the next complete message in
     * the builder, if one exists.
     * <p>
     * A message is considered complete when all of the information sent as a part of the
     * message is received and coalesced into a single {@code String[]}. Callers can determine
     * whether complete messages are available via the {@link #hasMoreMessages()} method.
     * <p>
     * If a complete message does not exist, this method's behavior is undefined.
     * Implementations are free to return {@code null}, empty {@code String[]},
     * prior or random messages, or throw {@code Exception}s.
     * <p>
     * This method guarantees that the returned completed message which be fragmented in
     * exactly the same order and pattern as the original message. Any implementation
     * which does not satisfy this guarantee should note that in its documentation.
     *
     * @return A {@code String[]} representing a single, complete message sent over the network,
     * if one exists. Otherwise, behavior is undefined.
     * @see #hasMoreMessages()
     */
    String[] nextMessage();
}
