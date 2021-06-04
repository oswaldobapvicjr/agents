package net.obvj.agents.util;

import java.util.function.BinaryOperator;

public class Functions
{
    private Functions()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    public static <T> BinaryOperator<T> firstWinsMerger()
    {
        return (first, last) -> first;
    }

    public static <T> BinaryOperator<T> lastWinsMerger()
    {
        return (first, last) -> last;
    }
}
