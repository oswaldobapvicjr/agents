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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object that represents a time interval, which consists of a duration and a
 * {@link TimeUnit}, with advanced text parsing logic.
 *
 * @author oswaldo.bapvic.jr
 */
public class TimeInterval
{
    private static final String EMPTY_STRING = "";
    private static final Pattern DIGITS_GROUP_PATTERN = Pattern.compile("\\d+");
    private static final Pattern LETTERS_GROUP_PATTERN = Pattern.compile("[a-zA-Z]+");

    private final int duration;
    private final TimeUnit timeUnit;

    /**
     * Builds a TimeInterval with a given duration and {@link TimeUnit}.
     *
     * @param duration the duration to set
     * @param timeUnit the {@link TimeUnit} to set
     */
    public TimeInterval(int duration, TimeUnit timeUnit)
    {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    /**
     * Builds a new TimeInterval with the same attributes from a preset object.
     *
     * @param source the source object to be copied; not null
     * @throws NullPointerException if the source TimeInterval is null
     */
    public TimeInterval(TimeInterval source)
    {
        this(source.getDuration(), source.getTimeUnit());
    }

    /**
     * Builds a TimerInterval by parsing the given string.
     * <p>
     * For example: all of the following formats are recognized (case ignored):
     * <ul>
     * <li>"1 second", or "1s"</li>
     * <li>"60 SECONDS", or "60s"</li>
     * <li>"25 minutes", or "25M"</li>
     * <li>"1 HOUR", or "1h"</li>
     * </ul>
     *
     * @param input the string to be parsed
     * @return a TimerInteral from the given input
     * @throws IllegalArgumentException if the string is not in a valid TimeInterval format
     */
    public static TimeInterval of(String input)
    {
        int digits = extractFirstDigitGroupFrom(input);
        String timeUnitDescription = extractFirstLetterGroupFrom(input);

        TimeUnit timeUnit = timeUnitDescription.isEmpty() ? TimeUnit.DEFAULT
                : TimeUnit.findByIdentifier(timeUnitDescription);

        return new TimeInterval(digits, timeUnit);
    }

    /**
     * Extracts the first group of digits found in the given string.
     * <p>
     * For example: the following call {@code extractFirstDigitGroupFromString("15s")} returns
     * {@code "15"}.
     *
     * @param input the source string; not null
     * @return the first group of digits found in the input string, as integer
     * @throws IllegalArgumentException if no digit found in the input string
     */
    protected static int extractFirstDigitGroupFrom(String input)
    {
        Matcher matcher = DIGITS_GROUP_PATTERN.matcher(input);
        if (matcher.find())
        {
            return Integer.parseInt(matcher.group(0));
        }
        throw Exceptions.illegalArgument("No digit found in input string: \"%s\"", input);
    }

    /**
     * Extracts the first group of letters found in the given string.
     * <p>
     * For example: the following call {@code extractFirstLetterGroupFromString("1 minute")}
     * returns {@code "minute"}.
     *
     * @param input the source string
     * @return the first group of letters found in the input string
     */
    protected static String extractFirstLetterGroupFrom(String input)
    {
        Matcher matcher = LETTERS_GROUP_PATTERN.matcher(input);
        if (matcher.find())
        {
            return matcher.group(0);
        }
        return EMPTY_STRING;
    }

    /**
     * Returns the duration of this {@link TimeUnit}.
     *
     * @return the duration
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * Returns the {@link TimeUnit} associated with this {@link TimeInterval}.
     *
     * @return the interval's time unit
     */
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Returns this time interval's duration, in milliseconds.
     *
     * @return the interval duration, in milliseconds
     */
    public long toMillis()
    {
        return timeUnit.toMillis(duration);
    }

    /**
     * Returns a human-friendly string representation of this {@link TimeInterval}, for
     * example: {@code "2 minute(s)"}.
     *
     * @return the string representation of this object
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuilder().append(duration).append(" ").append(timeUnit).toString();
    }

    /**
     * Returns a hash code value for the object, to support hash tables.
     *
     * @return the hash code
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(duration, timeUnit);
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof TimeInterval))
        {
            return false;
        }
        TimeInterval other = (TimeInterval) obj;
        return duration == other.duration && timeUnit == other.timeUnit;
    }

}
