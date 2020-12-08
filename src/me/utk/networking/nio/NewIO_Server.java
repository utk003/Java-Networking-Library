package me.utk.networking.nio;

import me.utk.networking.ServerImplementation;

public abstract class NewIO_Server {
    private NewIO_Server() {
    }

    public static ServerImplementation defaultImplementation() {
        return defaultImplementation(0);
    }
    public static ServerImplementation defaultImplementation(int port) {
        return null;
    }
}
