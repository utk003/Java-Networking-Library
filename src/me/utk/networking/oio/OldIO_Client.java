package me.utk.networking.oio;

import me.utk.networking.ClientImplementation;

public abstract class OldIO_Client {
    private OldIO_Client() {
    }

    /**
     * Create a new {@link ClientImplementation}.
     *
     * @return The newly generated {@code ClientImplementation}
     * @see ClientImplementation
     */
    public static ClientImplementation defaultImplementation() {
        return new OldIO_ClientImplementation();
    }
}
