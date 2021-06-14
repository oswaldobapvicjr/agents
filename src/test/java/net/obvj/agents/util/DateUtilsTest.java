package net.obvj.agents.util;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link DateUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
class DateUtilsTest
{
    private static final String STR_UTC = "UTC";

    @BeforeAll
    public static void setup()
    {
        Locale.setDefault(Locale.UK);
        TimeZone.setDefault(TimeZone.getTimeZone(STR_UTC));
    }

    private static Date toDate(int year, int month, int day, int hour, int minute, int second, int millisecond)
    {
        return toCalendar(year, month, day, hour, minute, second, millisecond).getTime();
    }

    private static Calendar toCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(STR_UTC));
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar;
    }

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(DateUtils.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void now_zonedDateTimeNotNull()
    {
        assertThat(DateUtils.now(), isA(ZonedDateTime.class));
    }

    /**
     * Test successful date formatting to common string format
     */
    @Test
    void formatDate_validDate_validString()
    {
        Date date = toDate(2019, 6, 12, 18, 15, 1, 123);
        assertThat(DateUtils.formatDate(date), is("2019-06-12T18:15:01"));
    }

    /**
     * Test date formatting with null date
     */
    @Test
    void formatDate_nullDate_stringContainingNull()
    {
        assertThat(DateUtils.formatDate((Date) null), is(DateUtils.NULL_STRING));
    }

    /**
     * Test successful calendar formatting to common string format
     */
    @Test
    void formatDate_validCalendar_validString()
    {
        Calendar calendar = toCalendar(2019, 6, 12, 18, 15, 1, 123);
        assertThat(DateUtils.formatDate(calendar), is("2019-06-12T18:15:01"));
    }

    /**
     * Test calendar formatting with null date
     */
    @Test
    void formatDate_nullCalendar_stringContainingNull()
    {
        assertThat(DateUtils.formatDate((Calendar) null), is(DateUtils.NULL_STRING));
    }

    /**
     * Test successful ZonedDateTime formatting to common string format
     */
    @Test
    void formatDate_validZonedDateTime_validString()
    {
        ZonedDateTime date = ZonedDateTime.of(2019, 6, 12, 18, 15, 1, 123000000, ZoneId.of(STR_UTC));
        assertThat(DateUtils.formatDate(date), is("2019-06-12T18:15:01"));
    }

    /**
     * Test successful exact start date calculation every 1 minute with the
     * {@code getNextExactDateEveryInterval(int, TimeUnit, Calendar)} method.
     * <p>
     * The resulting date must be:
     * <ul>
     * <li>minute = the next minute</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     * </ul>
     */
    @Test
    void getNextExactDateEveryInterval_1minuteAndValidCalendar_validDate()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 15, 1, 123);
        Date exactStartDate = DateUtils.getNextExactDateEveryInterval(1, TimeUnit.MINUTES, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 16, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 5 minutes with the
     * {@code getNextExactDateEveryInterval(int, TimeUnit, Calendar)} method.
     * <p>
     * The resulting date must be:
     * <ul>
     * <li>minute = the next multiple of 5</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     * </ul>
     */
    @Test
    void getNextExactDateEveryInterval_5minutesAndValidCalendar_validDate()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 16, 1, 123);
        Date exactStartDate = DateUtils.getNextExactDateEveryInterval(5, TimeUnit.MINUTES, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 20, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 30 minutes with the
     * {@code getNextExactDateEveryInterval(int, TimeUnit, Calendar)} method.
     * <p>
     * The resulting date must be:
     * <ul>
     * <li>minute = the next multiple of 30</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     * </ul>
     */
    @Test
    void getNextExactDateEveryInterval_30minutesAndValidCalendar_validDate()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 45, 1, 123);
        Date exactStartDate = DateUtils.getNextExactDateEveryInterval(30, TimeUnit.MINUTES, baseDate);
        assertThat(exactStartDate, (is(toDate(2019, 6, 12, 19, 0, 0, 0))));
    }

    /**
     * Test successful exact start date calculation every 1 hour with the
     * {@code getNextExactDateEveryInterval(int, TimeUnit, Calendar)} method.
     * <p>
     * The resulting date must be:
     * <ul>
     * <li>hour = the next hour</li>
     * <li>minute = 0</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     * </ul>
     */
    @Test
    void getNextExactDateEveryInterval_1hourAndValidCalendar_validDate()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 23, 38, 1, 123);
        Date exactStartDate = DateUtils.getNextExactDateEveryInterval(1, TimeUnit.HOURS, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 13, 0, 0, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 2 hours with the
     * {@code getNextExactDateEveryInterval(int, TimeUnit, Calendar)} method.
     * <p>
     * The resulting date must be:
     * <ul>
     * <li>hour = the next multiple of 2</li>
     * <li>minute = 0</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     * </ul>
     */
    @Test
    void getNextExactDateEveryInterval_2HoursAndValidCalendar_validDate()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 17, 16, 1, 123);
        Date exactStartDate = DateUtils.getNextExactDateEveryInterval(2, TimeUnit.HOURS, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 0, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 30 seconds with the
     * {@code getNextExactDateEveryInterval(int, TimeUnit, Date)} method.
     * <p>
     * The resulting date must be:
     * <ul>
     * <li>second = the next multiple of 30</li>
     * <li>millisecond = 0</li>
     * </ul>
     */
    @Test
    void getNextExactDateEveryInterval_30secondsAndValidDate_validDate()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 1, 29, 123);
        Date exactStartDate = DateUtils.getNextExactDateEveryInterval(30, TimeUnit.SECONDS, baseDate.getTime());
        assertThat(exactStartDate, (is(toDate(2019, 6, 12, 18, 1, 30, 0))));
    }

}
