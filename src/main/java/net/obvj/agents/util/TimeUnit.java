/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.agents.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Enumerates the supported time units for Timer agents execution.
 *
 * @author oswaldo.bapvic.jr
 */
public enum TimeUnit
{
    /**
     * Time unit representing one second
     */
    SECONDS(java.util.concurrent.TimeUnit.SECONDS,
            Calendar.SECOND,
            Arrays.asList("second", "seconds", "second(s)", "s"),
            "second(s)"),

    /**
     * Time unit representing sixty second
     */
    MINUTES(java.util.concurrent.TimeUnit.MINUTES,
            Calendar.MINUTE,
            Arrays.asList("minute", "minutes", "minute(s)", "m"),
            "minute(s)"),

    /**
     * Time unit representing sixty minutes
     */
    HOURS(java.util.concurrent.TimeUnit.HOURS,
            Calendar.HOUR_OF_DAY,
            Arrays.asList("hour", "hours", "hour(s)", "h"),
            "hour(s)");

    /**
     * The default time unit
     */
    public static final TimeUnit DEFAULT = TimeUnit.MINUTES;

    private final java.util.concurrent.TimeUnit javaTimeUnit;
    private final int calendarConstant;
    private final List<String> identifiers;
    private final String displayText;

    private TimeUnit(java.util.concurrent.TimeUnit javaTimeUnit, int calendarConstant, List<String> identifiers, String displayText)
    {
        this.javaTimeUnit = javaTimeUnit;
        this.calendarConstant = calendarConstant;
        this.identifiers = identifiers;
        this.displayText = displayText;
    }

    /**
     * Returns a {@link TimeUnit} that is identifiable by the given string.
     * <p>
     * For example, all of the following strings match to {@link TimeUnit#SECONDS}:
     * <ul>
     * <li>s, S</li>
     * <li>second, SECOND</li>
     * <li>seconds, SECONDS</li>
     * </ul>
     *
     * @param identifier a string that identified a Time Unit
     * @return a {@link TimeUnit}, not null
     * @throws IllegalArgumentException if no time unit matches the given identifier
     */
    public static TimeUnit findByIdentifier(String identifier)
    {
        return Arrays.stream(TimeUnit.values())
                .filter(timeUnit -> timeUnit.isIdentifiableBy(identifier))
                .findFirst()
                .orElseThrow(() -> Exceptions.illegalArgument("Invalid time unit identifier: \"%s\"", identifier));
    }

    /**
     * Checks a given string against a list of known identifiers for a {@code TimeUnit},
     * returning {@code true} if a match is found.
     *
     * @param identifier the identifier to be checked
     * @return {@code true} if this {@code TimeUnit} is identifiable by the given identifier;
     *         {@code false}, otherwise.
     */
    public boolean isIdentifiableBy(String identifier)
    {
        return StringUtils.isNotEmpty(identifier)
                && identifiers.stream()
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
     * Returns a string representation of this {@code TimeUnit}.
     *
     * @return the string representation of this object
     */
    @Override
    public String toString()
    {
        return displayText;
    }

    /**
     * Converts the given amount into milliseconds.
     *
     * @param amount the amount to be converted
     * @return the amount converted into milliseconds
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
