package me.utk.networking.oio;

import me.utk.networking.ServerImplementation;

public abstract class OldIO_Server {
    private OldIO_Server() {
    }

    /**
     * Create a new {@link ServerImplementation}
     * with a randomly generated connection port.
     *
     * @return The newly generated {@code ServerImplementation}
     * @throws IllegalStateException If the {@code ServerImplementation} cannot be created
     * @see #defaultImplementation(int)
     * @see ServerImplementation
     */
    public static ServerImplementation defaultImplementation() {
        return new OldIO_ServerImplementation(0);
    }
    /**
     * Create a new {@link ServerImplementation}
     * with the specified connection port.
     *
     * @param port The port to create the server on (0 creates on a random port)
     * @return The newly generated {@code ServerImplementation}
     * @throws IllegalStateException If the {@code ServerImplementation} cannot be created
     * @see ServerImplementation
     */
    public static ServerImplementation defaultImplementation(int port) {
        return new OldIO_ServerImplementation(port);
    }
}
