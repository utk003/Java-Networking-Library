package me.utk.networking;

public abstract class ClientImplementation {
    public abstract void sendMessages(String... messages);
    public abstract MessageBuilder getMessageBuilder();

    public abstract boolean connect(String addressAndPort);
    public abstract boolean connect(String addressAndPort, String passcode);
    public abstract boolean connect(String address, int port);
    public abstract boolean connect(String address, int port, String passcode);

    public abstract void killClient();
}
