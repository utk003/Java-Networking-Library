package me.utk.networking.oio;

import me.utk.networking.ServerSideClient;

import java.net.Socket;

class OldIO_ServerSideClient extends ServerSideClient {
    private final Socket SOCKET;
    private final OldIO_MessageBuilder BUILDER;

    OldIO_ServerSideClient(Socket socket) {
        SOCKET = socket;
        BUILDER = new OldIO_MessageBuilder();
    }

    @Override
    public Socket getSocket() {
        return SOCKET;
    }

    @Override
    public void sendMessages(String... messages) {
        OldIO_Util.sendMessages(getSocket(), messages);
    }

    @Override
    public OldIO_MessageBuilder getMessageBuilder() {
        return BUILDER;
    }
}
