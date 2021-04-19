package net.obvj.agents.exception;

/**
 * An exception that can be thrown in cases of invalid class setup.
 *
 * @author oswaldo.bapvic.jr
 */
public class InvalidClassException extends RuntimeException
{
    private static final long serialVersionUID = 6986207128590595522L;

    /**
     * Constructs a new InvalidClassException with the specified detail message. A detail
     * message is a String that describes this particular exception.
     *
     * @param message the String that contains a detailed message
     */
    public InvalidClassException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new InvalidClassException with the specified detail message and cause.
     *
     * @param message the detail message, which is saved for later retrieval by the
     *                {@link Throwable#getMessage()} method.
     * @param cause   the cause, which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method. (A {@code null} value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public InvalidClassException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidClassException with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())}. This constructor is useful for
     * exceptions that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). (A {@code null} value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public InvalidClassException(Throwable cause)
    {
        super(cause);
    }
}
