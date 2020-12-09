package me.utk.networking.oio;

import me.utk.networking.ServerImplementation;

/**
 * A utility class which provides a default implementation of {@link ServerImplementation}.
 * <p>
 * This class provides a default implementation of {@code ServerImplementation}, which can be
 * created and accessed via the {@code defaultImplementation} methods provided by this class.
 * <p>
 * The implementation-specific details of this default implementation can be found
 * in the documentation of the {@link #defaultImplementation(int)} method.
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see #defaultImplementation()
 * @see #defaultImplementation(int)
 * @see ServerImplementation
 */
public abstract class OldIO_Server {
    private OldIO_Server() {
    }

    /**
     * Creates a new {@link ServerImplementation} with a randomly generated connection port.
     * <p>
     * The implementation-specific details of default implementation can be found
     * in the documentation of the {@link #defaultImplementation(int)} method.
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
     * Creates a new {@link ServerImplementation} with the specified connection port.
     * <p>
     * The default implementation of the {@code ServerImplementation} meets all of the guarantees
     * and conditions required by the {@code ServerImplementation} definition.
     * <p>
     * Of primary importance, creating a new default {@code ServerImplementation} launches
     * three helper threads for accepting, verifying, and communicating with incoming
     * connections and clients. These threads can only be stopped via the
     * {@link ServerImplementation#closeServer()} method.
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
