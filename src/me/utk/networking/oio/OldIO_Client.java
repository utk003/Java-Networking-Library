package me.utk.networking.oio;

import me.utk.networking.ClientImplementation;

/**
 * A utility class which provides a default implementation of {@link ClientImplementation}.
 * <p>
 * This class provides a default implementation of {@code ClientImplementation}, which can be
 * created and accessed via the {@code defaultImplementation} method provided by this class.
 * <p>
 * The implementation-specific details of this default implementation can be found
 * in the documentation of the {@link #defaultImplementation()} method.
 *
 * @author Utkarsh Priyam
 * @version December 8, 2020
 * @see #defaultImplementation()
 * @see ClientImplementation
 */
public abstract class OldIO_Client {
    private OldIO_Client() {
    }

    /**
     * Creates a new {@link ClientImplementation}.
     * <p>
     * The default implementation of the {@code ClientImplementation} meets all of the guarantees
     * and conditions required by the {@code ClientImplementation} definition.
     * <p>
     * Of primary importance, creating a new default {@code ServerImplementation} launches
     * two helper threads for connecting to and communicating with the server. These threads
     * can only be stopped via the {@link ClientImplementation#closeClient()} method.
     *
     * @return The newly generated {@code ClientImplementation}
     * @see ClientImplementation
     */
    public static ClientImplementation defaultImplementation() {
        return new OldIO_ClientImplementation();
    }
}
