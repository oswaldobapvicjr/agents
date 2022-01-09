package net.obvj.agents.util;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link CommonRegEx}.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.3.0
 */
class CommonRegExTest
{

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(CommonRegEx.class, instantiationNotAllowed().throwing(UnsupportedOperationException.class)
                .withMessage("Instantiation not allowed"));
    }
}
