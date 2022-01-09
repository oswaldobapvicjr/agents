package net.obvj.agents.util.logging;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link LogUtils}.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.3.0
 */
class LogUtilsTest
{

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(LogUtils.class, instantiationNotAllowed().throwing(UnsupportedOperationException.class)
                .withMessage("Instantiation not allowed"));
    }

}
