package me.utk.networking;

public abstract class MessageBuilder {
    public abstract boolean hasMoreMessages();
    public abstract String[] nextMessage();
}
