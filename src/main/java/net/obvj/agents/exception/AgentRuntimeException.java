package net.obvj.agents.exception;

/**
 * An exception that can be thrown by agents at runtime.
 * 
 * @author oswaldo.bapvic.jr
 */
public class AgentRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 6615162498994428780L;

    /**
     * Constructs an AgentRuntimeException with the specified detail message. A detail
     * message is a String that describes this particular exception.
     *
     * @param message the String that contains a detailed message
     */
    public AgentRuntimeException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message, which is saved for later retrieval by the
     *                {@link Throwable#getMessage()} method.
     * @param cause   the cause, which is saved for later retrieval by the
     *                {@link Throwable#getCause()} method. (A {@code null} value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     */
    public AgentRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())}. This constructor is useful for
     * exceptions that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link Throwable#getCause()} method). (A {@code null} value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public AgentRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
