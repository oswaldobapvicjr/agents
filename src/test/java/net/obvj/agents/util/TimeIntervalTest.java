package net.obvj.agents.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link TimeInterval} class.
 *
 * @author oswaldo.bapvic.jr
 */
class TimeIntervalTest
{
    private void assertTimeIntervalOf(int expectedDuration, TimeUnit expectedTimeUnit, String input)
    {
        TimeInterval timeInterval = TimeInterval.parseString2TimeInterval(input);
        assertThat(timeInterval.getDuration(), is(expectedDuration));
        assertThat(timeInterval.getTimeUnit(), is(expectedTimeUnit));
    }

    @Test
    void extractFirstDigitGroupFrom_validStrings_success()
    {
        assertThat(TimeInterval.extractFirstDigitGroupFrom("1minute"), is(1));
        assertThat(TimeInterval.extractFirstDigitGroupFrom("5m"), is(5));
        assertThat(TimeInterval.extractFirstDigitGroupFrom("10minutes"), is(10));
        assertThat(TimeInterval.extractFirstDigitGroupFrom("15 minutes"), is(15));
        assertThat(TimeInterval.extractFirstDigitGroupFrom("20 m"), is(20));
        assertThat(TimeInterval.extractFirstDigitGroupFrom("25"), is(25));
        assertThat(TimeInterval.extractFirstDigitGroupFrom(" 30 "), is(30));
        assertThat(TimeInterval.extractFirstDigitGroupFrom("35.5s"), is(35));
        assertThat(TimeInterval.extractFirstDigitGroupFrom("40,5s"), is(40));
    }

    @Test
    void extractFirstDigitGroupFrom_invalidString_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> TimeInterval.extractFirstDigitGroupFrom("minute"));
    }

    @Test
    void extractFirstDigitGroupFrom_emptyString_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> TimeInterval.extractFirstDigitGroupFrom(""));
    }

    @Test
    void extractFirstLetterGroupFrom_validStrings_success()
    {
        assertThat(TimeInterval.extractFirstLetterGroupFrom("1minute"), is("minute"));
        assertThat(TimeInterval.extractFirstLetterGroupFrom("10 minutes"), is("minutes"));
        assertThat(TimeInterval.extractFirstLetterGroupFrom("5m"), is("m"));
        assertThat(TimeInterval.extractFirstLetterGroupFrom("15 H"), is("H"));
        assertThat(TimeInterval.extractFirstLetterGroupFrom("20 seconds "), is("seconds"));
        assertThat(TimeInterval.extractFirstLetterGroupFrom("25"), is(""));
        assertThat(TimeInterval.extractFirstLetterGroupFrom(" 30 "), is(""));
        assertThat(TimeInterval.extractFirstLetterGroupFrom("35.5 hours"), is("hours"));
        assertThat(TimeInterval.extractFirstLetterGroupFrom("40,5s"), is("s"));
    }

    @Test
    void of_validStrings_validAmountsAndTimeUnits()
    {
        assertTimeIntervalOf(1, TimeUnit.MINUTES, "1minute");
        assertTimeIntervalOf(5, TimeUnit.MINUTES, "5m");
        assertTimeIntervalOf(10, TimeUnit.MINUTES, "10minutes");
        assertTimeIntervalOf(15, TimeUnit.MINUTES, "15 Minutes");
        assertTimeIntervalOf(20, TimeUnit.MINUTES, "20 m");
        assertTimeIntervalOf(25, TimeUnit.MINUTES, "25");
        assertTimeIntervalOf(30, TimeUnit.MINUTES, " 30 ");
        assertTimeIntervalOf(35, TimeUnit.MINUTES, "35_MINUTE");
        assertTimeIntervalOf(1, TimeUnit.HOURS, "1H");
        assertTimeIntervalOf(2, TimeUnit.HOURS, "2h");
        assertTimeIntervalOf(12, TimeUnit.HOURS, "12-HOURS");
        assertTimeIntervalOf(3, TimeUnit.HOURS, "hour=3");
        assertTimeIntervalOf(15, TimeUnit.SECONDS, "seconds=15");
        assertTimeIntervalOf(3, TimeUnit.SECONDS, "SeCoNd:3");
    }

    @Test
    void of_invalidString_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> TimeInterval.parseString2TimeInterval("1byte"));
    }

    @Test
    void of_emptyString_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> TimeInterval.parseString2TimeInterval(""));
    }

    @Test
    void equalsAndHashCode_similarObjects_handledAsEqualObject()
    {
        Set<TimeInterval> set = new HashSet<>();
        set.add(TimeInterval.parseString2TimeInterval("1 second"));
        set.add(TimeInterval.parseString2TimeInterval("1s"));
        set.add(TimeInterval.parseString2TimeInterval("1SECOND"));
        assertThat(set.size(), is(1));
    }

    @Test
    void equals_multipleTimeIntervals_success()
    {
        TimeInterval tenSeconds = TimeInterval.parseString2TimeInterval("10 seconds");
        assertThat(tenSeconds.equals(tenSeconds), is(true));
        assertThat(tenSeconds.equals(TimeInterval.parseString2TimeInterval("10 s")), is(true));
        assertThat(tenSeconds.equals(TimeInterval.parseString2TimeInterval("1 seconds")), is(false));
        assertThat(tenSeconds.equals(TimeInterval.parseString2TimeInterval("10 minutes")), is(false));
        assertThat(tenSeconds.equals(null), is(false));
        assertThat(tenSeconds.equals(new Object()), is(false));
    }

    @Test
    void toMillis_validTimeInterval_sucess()
    {
        assertThat(TimeInterval.parseString2TimeInterval("2seconds").toMillis(), is(2000L));
    }

    @Test
    void constructor_validSourceTimeInterval_equalButNotSameObject()
    {
        TimeInterval original = TimeInterval.parseString2TimeInterval("50 seconds");
        TimeInterval clone = new TimeInterval(original);
        assertEquals(original, clone);
        assertNotSame(original, clone);
    }

    @Test
    void toString_validTimeInterval_success()
    {
        assertThat(TimeInterval.parseString2TimeInterval("10s").toString(), is("10 second(s)"));
    }

}
