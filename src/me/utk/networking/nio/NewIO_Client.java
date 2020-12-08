package me.utk.networking.nio;

import me.utk.networking.ClientImplementation;

public abstract class NewIO_Client {
    private NewIO_Client() {
    }

    public static ClientImplementation defaultImplementation() {
        return null;
    }
}
