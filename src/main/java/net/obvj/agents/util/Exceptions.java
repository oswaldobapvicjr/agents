package net.obvj.agents.util;

import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.exception.AgentRuntimeException;
import net.obvj.agents.exception.InvalidClassException;

/**
 * Shorthands creating exceptions with a formatted message.
 *
 * @author oswaldo.bapvic.jr
 */
public final class Exceptions
{
    private Exceptions()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Creates an {@link IllegalArgumentException} with a formatted message.
     *
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link IllegalArgumentException} with a formatted message
     */
    public static IllegalArgumentException illegalArgument(final String format, final Object... args)
    {
        return new IllegalArgumentException(String.format(format, args));
    }

    /**
     * Creates an {@link IllegalArgumentException} with a cause and a formatted message.
     *
     * @param cause  the cause to be set
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link IllegalArgumentException} with given cause a formatted message
     */
    public static IllegalArgumentException illegalArgument(final Throwable cause, final String format,
            final Object... args)
    {
        return new IllegalArgumentException(String.format(format, args), cause);
    }

    /**
     * Creates an {@link IllegalStateException} with a formatted message.
     *
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link IllegalStateException} with a formatted message
     */
    public static IllegalStateException illegalState(final String format, final Object... args)
    {
        return new IllegalStateException(String.format(format, args));
    }

    /**
     * Creates an {@link IllegalStateException} with a cause and a formatted message.
     *
     * @param cause  the cause to be set
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link IllegalStateException} with given cause and formatted message
     */
    public static IllegalStateException illegalState(final Throwable cause, final String format, final Object... args)
    {
        return new IllegalStateException(String.format(format, args), cause);
    }

    /**
     * Creates a {@link InvalidClassException} with a formatted message.
     *
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return a {@link InvalidClassException} with a formatted message
     */
    public static InvalidClassException invalidClass(final String format, final Object... args)
    {
        return new InvalidClassException(String.format(format, args));
    }

    /**
     * Creates a {@link InvalidClassException} with a cause and a formatted message.
     *
     * @param cause  the cause to be set
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return a {@link InvalidClassException} with given cause and formatted message
     */
    public static InvalidClassException invalidClass(final Throwable cause, final String format, final Object... args)
    {
        return new InvalidClassException(String.format(format, args), cause);
    }

    /**
     * Creates an {@link AgentConfigurationException} with a formatted message.
     *
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link AgentConfigurationException} with a formatted message
     */
    public static AgentConfigurationException agentConfiguration(final String format, final Object... args)
    {
        return new AgentConfigurationException(String.format(format, args));
    }

    /**
     * Creates an {@link AgentConfigurationException} with a cause and a formatted message.
     *
     * @param cause  the cause to be set
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link AgentConfigurationException} with given cause and formatted message
     */
    public static AgentConfigurationException agentConfiguration(final Throwable cause, final String format,
            final Object... args)
    {
        return new AgentConfigurationException(String.format(format, args), cause);
    }

    /**
     * Creates an {@link AgentRuntimeException} with a formatted message.
     *
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link AgentRuntimeException} with a formatted message
     */
    public static AgentRuntimeException agentRuntime(final String format, final Object... args)
    {
        return new AgentRuntimeException(String.format(format, args));
    }

    /**
     * Creates an {@link AgentRuntimeException} with a cause and a formatted message.
     *
     * @param cause  the cause to be set
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link AgentRuntimeException} with given cause and formatted message
     */
    public static AgentRuntimeException agentRuntime(final Throwable cause, final String format, final Object... args)
    {
        return new AgentRuntimeException(String.format(format, args), cause);
    }

}
