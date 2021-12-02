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

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Utility methods for working with dates
 *
 * @author oswaldo.bapvic.jr
 */
public class DateUtils
{
    protected static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    protected static final String NULL_STRING = "null";


    private DateUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns the current date-time as a {@link ZonedDateTime}, using the system clock.
     *
     * @return a {@link ZonedDateTime} for the current date-time, not null
     */
    public static ZonedDateTime now()
    {
        return ZonedDateTime.now();
    }

    /**
     * Formats the given calendar using a standardized date format.
     *
     * @param calendar the calendar to be formatted
     * @return a formatted string
     */
    public static String formatDate(Calendar calendar)
    {
        return calendar != null ? formatDate(calendar.getTime()) : NULL_STRING;
    }

    /**
     * Formats the given {@link ZonedDateTime} using a standardized date format.
     *
     * @param zonedDateTime the object to be formatted
     * @return a formatted string
     */
    public static String formatDate(ZonedDateTime zonedDateTime)
    {
        return zonedDateTime != null ? formatDate(Date.from(zonedDateTime.toInstant())) : NULL_STRING;
    }

    /**
     * Formats the given date using a standardized date format.
     *
     * @param date the date to be formatted
     * @return a formatted string
     */
    public static String formatDate(Date date)
    {
        return date != null ? DateFormatUtils.format(date, DEFAULT_DATE_FORMAT) : NULL_STRING;
    }

    /**
     * Return the next exact date for a given interval and time unit after the current time.
     * <p>
     * For example:
     * <ul>
     * <li>if current time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>30
     * seconds</strong>, the return will be 23:28:30</li>
     *
     * <li>if current time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>1
     * minute</strong>, the return will be 23:39:00</li>
     *
     * <li>if current time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>1
     * hour</strong>, the return will be 00:00:00 (next day)</li>
     * </ul>
     *
     * @param interval the maximum interval for each execution
     * @param timeUnit the given interval's time unit
     * @return the next exact date for the given interval and time unit
     */
    public static Date getNextExactDateEveryInterval(int interval, TimeUnit timeUnit)
    {
        return getNextExactDateEveryInterval(interval, timeUnit, Calendar.getInstance());
    }

    /**
     * Return the next exact date given a specific interval and time unit after the specified
     * date.
     * <p>
     * For example:
     * <ul>
     * <li>if the source time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>30
     * seconds</strong>, the return will be 23:28:30</li>
     *
     * <li>if the source time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>1
     * minute</strong>, the return will be 23:39:00</li>
     *
     * <li>if the source time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>1
     * hour</strong>, the return will be 00:00:00 (next day)</li>
     * </ul>
     *
     * @param interval the maximum interval for each execution
     * @param timeUnit the given interval's time unit
     * @param date     the source {@link Date} to be processed
     * @return the next date for the given interval and time unit
     */
    public static Date getNextExactDateEveryInterval(int interval, TimeUnit timeUnit, Date date)
    {
        Objects.requireNonNull(date, "The source date must not be null");
        Calendar calendar = toCalendar(date);
        return getNextExactDateEveryInterval(interval, timeUnit, calendar);
    }

    /**
     * Return the next exact date given a specific interval and time unit after the specified
     * {@link Calendar} instance.
     * <p>
     * For example:
     * <ul>
     * <li>if the source time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>30
     * seconds</strong>, the return will be 23:28:30</li>
     *
     * <li>if the source time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>1
     * minute</strong>, the return will be 23:39:00</li>
     *
     * <li>if the source time is 23:38:26 (MM:SS:mi), and the interval is set to <strong>1
     * hour</strong>, the return will be 00:00:00 (next day)</li>
     * </ul>
     *
     * @param interval the maximum interval for each execution
     * @param timeUnit the given interval's time unit
     * @param calendar the source {@link Calendar} instance
     * @return the next date for the given interval and time unit
     */
    @SuppressWarnings("squid:S128")
    public static Date getNextExactDateEveryInterval(int interval, TimeUnit timeUnit, Calendar calendar)
    {
        Objects.requireNonNull(calendar, "The source calendar must not be null");
        Calendar nextDate = getClonedCalendar(calendar);

        int time = calendar.get(timeUnit.getCalendarConstant());
        int timeDiff = (time % interval == 0) ? 0 : interval - time % interval;

        nextDate.add(timeUnit.getCalendarConstant(), timeDiff);

        if (nextDate.before(calendar) || nextDate.equals(calendar))
        {
            nextDate.add(timeUnit.getCalendarConstant(), interval);
        }

        switch (timeUnit)
        {
        // NOTE: It is safe to ignore SonarQube squid:S128 here
        // We really want to continue executing the statements of the subsequent cases on purpose
        case HOURS:
            nextDate.set(Calendar.MINUTE, 0);
        case MINUTES:
            nextDate.set(Calendar.SECOND, 0);
        case SECONDS:
            nextDate.set(Calendar.MILLISECOND, 0);
        }

        return nextDate.getTime();
    }

    /**
     * Creates and returns a clone of the given calendar object.
     *
     * @param calendar the source {@link Calendar}
     * @return a copy of the given calendar instance
     */
    public static Calendar getClonedCalendar(Calendar calendar)
    {
        return calendar != null ? (Calendar) calendar.clone() : null;
    }

    private static Calendar toCalendar(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

}
