package net.obvj.agents.exception;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link AgentConfigurationException} class.
 *
 * @author oswaldo.bapvic.jr
 */
class AgentConfigurationExceptionTest
{
    private static final String DETAILED_MESSAGE1 = "detailedMessage1";
    private static final String ROOT_CAUSE_MESSAGE1 = "rootCauseMessage1";

    @Test
    void constructor_validMessage_validMessageAndNoCause()
    {
        assertThat(() ->
        {
            throw new AgentConfigurationException(DETAILED_MESSAGE1);
        },
        throwsException(AgentConfigurationException.class)
                .withMessage(DETAILED_MESSAGE1)
                .withCause(null));
    }

    @Test
    void constructor_validCause_validMessageAndCause()
    {
        assertThat(() ->
        {
            throw new AgentConfigurationException(new IllegalArgumentException(ROOT_CAUSE_MESSAGE1));
        },
        throwsException(AgentConfigurationException.class)
                .withMessage(endsWith(ROOT_CAUSE_MESSAGE1))
                .withCause(IllegalArgumentException.class));
    }

    @Test
    void constructor_validMessageAndCause_validMessageAndCause()
    {
        assertThat(() ->
        {
            throw new AgentConfigurationException(DETAILED_MESSAGE1, new IllegalArgumentException(ROOT_CAUSE_MESSAGE1));
        },
        throwsException(AgentConfigurationException.class)
                .withMessage(DETAILED_MESSAGE1)
                .withCause(IllegalArgumentException.class));
    }

}
