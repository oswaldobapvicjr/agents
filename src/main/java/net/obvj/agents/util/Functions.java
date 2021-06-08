package net.obvj.agents.util;

import java.util.function.BinaryOperator;

/**
 * This class provides common functions for use with Streams and Collectors.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.2.0
 */
public class Functions
{
    private Functions()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * A merger operation upon two operands of the same type, typically for handling
     * collisions between values associated with the same key in a Map, where the first object
     * is always applied.
     *
     * @param <T> the type of the operands and result of the operator
     * @return the first object, always
     */
    public static <T> BinaryOperator<T> firstWinsMerger()
    {
        return (first, last) -> first;
    }

    /**
     * A merger operation upon two operands of the same type, typically for handling
     * collisions between values associated with the same key in a Map, where the second
     * object is always applied.
     *
     * @param <T> the type of the operands and result of the operator
     * @return the second object, always
     */
    public static <T> BinaryOperator<T> lastWinsMerger()
    {
        return (first, last) -> last;
    }
}
