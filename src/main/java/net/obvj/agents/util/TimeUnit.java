package net.obvj.agents.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Enumerates the supported time units for Timer agents execution.
 *
 * @author oswaldo.bapvic.jr
 */
public enum TimeUnit
{
    SECONDS(java.util.concurrent.TimeUnit.SECONDS, Calendar.SECOND, Arrays.asList("second", "seconds", "s"),
            "second(s)"),
    MINUTES(java.util.concurrent.TimeUnit.MINUTES, Calendar.MINUTE, Arrays.asList("minute", "minutes", "m"),
            "minute(s)"),
    HOURS(java.util.concurrent.TimeUnit.HOURS, Calendar.HOUR_OF_DAY, Arrays.asList("hour", "hours", "h"), "hour(s)");

    public static final TimeUnit DEFAULT = TimeUnit.MINUTES;

    private final java.util.concurrent.TimeUnit javaTimeUnit;
    private final int calendarConstant;
    private final List<String> identifiers;
    private final String displayText;

    private TimeUnit(java.util.concurrent.TimeUnit javaTimeUnit, int calendarField, List<String> aliases,
            String strText)
    {
        this.javaTimeUnit = javaTimeUnit;
        this.calendarConstant = calendarField;
        this.identifiers = aliases;
        this.displayText = strText;
    }

    /**
     * Returns a {@link TimeUnit} that is identifiable by the given string.
     * <p>
     * For example, all of the following strings match to {@link TimeUnit#SECONDS}:
     * <ul>
     * <li>second, SECOND</li>
     * <li>seconds, SECONDS</li>
     * <li>s, S</li>
     * </ul>
     *
     * @param identifier a string that identified a Time Unit
     * @return a {@link TimeUnit}, always
     * @throws IllegalArgumentException if no time unit matches the given identifier
     */
    public static TimeUnit findByIdentifier(String identifier)
    {
        return Arrays.stream(TimeUnit.values()).filter(timeUnit -> isTimeUnitIdentifiableBy(identifier, timeUnit))
                .findFirst()
                .orElseThrow(() -> Exceptions.illegalArgument("Invalid time unit identifier: \"%s\"", identifier));
    }

    private static boolean isTimeUnitIdentifiableBy(String identifier, TimeUnit timeUnit)
    {
        return timeUnit.identifiers.stream()
                .anyMatch(timeUnitIdentifier -> timeUnitIdentifier.equalsIgnoreCase(identifier));
    }

    /**
     * Returns the {@link Calendar} constant associated with this Time Unit.
     *
     * @return the {@link Calendar} constant
     */
    public int getCalendarConstant()
    {
        return calendarConstant;
    }

    /**
     * Returns a human-friendly string representation of this {@link TimeInterval}, for
     * example: {@code "1 MINUTE"}.
     *
     * @return the string representation of this object
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return displayText;
    }

    /**
     * Converts the given time duration to milliseconds.
     *
     * @param amount the time duration to be converted
     * @return the converted amount
     * @since 2.0
     */
    public long toMillis(long amount)
    {
        return javaTimeUnit.toMillis(amount);
    }

    /**
     * Converts the given time duration from a source Time Unit to this Time Unit.
     *
     * @param amount         the time duration amount to be converted
     * @param sourceTimeUnit the time unit of the {@code duration} parameter
     * @return the converted amount
     * @since 2.0
     */
    public long convert(long amount, TimeUnit sourceTimeUnit)
    {
        return javaTimeUnit.convert(amount, sourceTimeUnit.javaTimeUnit);
    }

}
