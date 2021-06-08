package net.obvj.agents.util;

import static net.obvj.agents.util.Functions.firstWinsMerger;
import static net.obvj.agents.util.Functions.lastWinsMerger;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Functions} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.2.0
 */
class FunctionsTest
{
    private static final String STRING1 = "value1";
    private static final String STRING2 = "value2";

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(Functions.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void firstWinsMerger_firstArgument()
    {
        assertThat(firstWinsMerger().apply(STRING1, STRING2), is(equalTo(STRING1)));
    }

    @Test
    void lastWinsMerger_lastArgument()
    {
        assertThat(lastWinsMerger().apply(STRING1, STRING2), is(equalTo(STRING2)));
    }

}
