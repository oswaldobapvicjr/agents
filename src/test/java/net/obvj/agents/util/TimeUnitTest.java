package net.obvj.agents.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link TimeUnit} class.
 *
 * @author oswaldo.bapvic.jr
 */
class TimeUnitTest
{
    /**
     * Test TimeUnit identification based on configured string identifiers
     */
    @Test
    void findByIdentifier_knownIdentifiers_success()
    {
        assertThat(TimeUnit.findByIdentifier("second"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("SECOND"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("seconds"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("SECONDS"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("s"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("S"), is(TimeUnit.SECONDS));

        assertThat(TimeUnit.findByIdentifier("minute"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("MINUTE"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("minutes"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("MINUTES"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("m"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("M"), is(TimeUnit.MINUTES));

        assertThat(TimeUnit.findByIdentifier("hour"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("HOUR"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("hours"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("HOURS"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("h"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("H"), is(TimeUnit.HOURS));
    }

    /**
     * Test TimeUnit identification for an unknown string identifier
     */
    @Test
    void findByIdentifier_unknownIdentifier_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> TimeUnit.findByIdentifier("x"));
    }

    /**
     * Test TimeUnit identification for null identifier
     */
    @Test
    void findByIdentifier_null_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> TimeUnit.findByIdentifier(null));
    }

    @Test
    void isIdentifiableBy_validStrings_true()
    {
        assertThat(TimeUnit.HOURS.isIdentifiableBy("h"), is(true));
        assertThat(TimeUnit.HOURS.isIdentifiableBy("H"), is(true));
        assertThat(TimeUnit.HOURS.isIdentifiableBy("hour"), is(true));
        assertThat(TimeUnit.HOURS.isIdentifiableBy("HoUr"), is(true));
        assertThat(TimeUnit.HOURS.isIdentifiableBy("hOuR(s)"), is(true));
    }

    @Test
    void isIdentifiableBy_invalidStrings_false()
    {
        assertThat(TimeUnit.HOURS.isIdentifiableBy("X"), is(false));
        assertThat(TimeUnit.HOURS.isIdentifiableBy(""), is(false));
        assertThat(TimeUnit.HOURS.isIdentifiableBy(null), is(false));
    }

    /**
     * Test Calendar constant associations by TimeUnit
     */
    @Test
    void getCalendarConstant_validContants()
    {
        assertThat(TimeUnit.SECONDS.getCalendarConstant(), is(Calendar.SECOND));
        assertThat(TimeUnit.MINUTES.getCalendarConstant(), is(Calendar.MINUTE));
        assertThat(TimeUnit.HOURS.getCalendarConstant(), is(Calendar.HOUR_OF_DAY));
    }

    /**
     * Test display strings returned by TimeUnit
     */
    @Test
    void toString_validStrings()
    {
        assertThat(TimeUnit.SECONDS.toString(), is("second(s)"));
        assertThat(TimeUnit.MINUTES.toString(), is("minute(s)"));
        assertThat(TimeUnit.HOURS.toString(), is("hour(s)"));
    }

    /**
     * Test time unit conversion to milliseconds
     */
    @Test
    void toMillis_multiple_validNumbers()
    {
        assertThat(TimeUnit.SECONDS.toMillis(1), is(1000l));
        assertThat(TimeUnit.MINUTES.toMillis(1), is(60000l));
        assertThat(TimeUnit.HOURS.toMillis(1), is(3600000l));
    }

    /**
     * Test time unit conversion, based on another source time unit
     */
    @Test
    void convert_multipleAmountsAndTimeUnits_validNumbers()
    {
        assertThat(TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES), is(60l));
        assertThat(TimeUnit.SECONDS.convert(1, TimeUnit.HOURS), is(3600l));
        assertThat(TimeUnit.MINUTES.convert(1, TimeUnit.HOURS), is(60l));
    }
}
