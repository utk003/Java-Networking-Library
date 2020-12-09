package me.utk.networking.oio;

import me.utk.networking.ServerSideClient;

import java.net.Socket;

class OldIO_ServerSideClient implements ServerSideClient {
    private final Socket SOCKET;
    private final OldIO_MessageBuilder BUILDER;

    OldIO_ServerSideClient(Socket socket) {
        SOCKET = socket;
        BUILDER = new OldIO_MessageBuilder();
    }

    Socket getSocket() {
        return SOCKET;
    }

    @Override
    public void sendMessages(String... messages) {
        OldIO_Util.sendMessages(SOCKET, messages);
    }

    @Override
    public OldIO_MessageBuilder getMessageBuilder() {
        return BUILDER;
    }
}
