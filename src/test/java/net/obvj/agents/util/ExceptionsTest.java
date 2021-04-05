package net.obvj.agents.util;

import static net.obvj.junit.utils.TestUtils.assertException;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.exception.AgentRuntimeException;

/**
 * Unit tests for the {@link Exceptions} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
class ExceptionsTest
{
    private static final String MSG_PATTERN = "arg1=%s,arg2=%s";
    private static final String ARG1 = "abc";
    private static final String ARG2 = "123";
    private static final String EXPECTED_MSG = "arg1=abc,arg2=123";

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(Exceptions.class, instantiationNotAllowed());
    }

    @Test
    void illegalArgument_messageAndParams_validMessage()
    {
        assertException(IllegalArgumentException.class, EXPECTED_MSG,
                Exceptions.illegalArgument(MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    void illegalArgument_messageAndParamsAndCause_validMessageAndCause()
    {
        assertException(IllegalArgumentException.class, EXPECTED_MSG, NullPointerException.class,
                Exceptions.illegalArgument(new NullPointerException(), MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    void illegalState_messageAndParams_validMessage()
    {
        assertException(IllegalStateException.class, EXPECTED_MSG, Exceptions.illegalState(MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    void illegalState_messageAndParamsAndCause_validMessageAndCause()
    {
        assertException(IllegalStateException.class, EXPECTED_MSG, NullPointerException.class,
                Exceptions.illegalState(new NullPointerException(), MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    void agentConfiguration_messageAndParams_validMessage()
    {
        assertException(AgentConfigurationException.class, EXPECTED_MSG, null,
                Exceptions.agentConfiguration(MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    void agentConfiguration_messageAndParamsAndCause_validMessageAndCause()
    {
        assertException(AgentConfigurationException.class, EXPECTED_MSG, NullPointerException.class,
                Exceptions.agentConfiguration(new NullPointerException(), MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    void agentRuntime_messageAndParams_validMessage()
    {
        assertException(AgentRuntimeException.class, EXPECTED_MSG, null,
                Exceptions.agentRuntime(MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    void agentRuntime_messageAndParamsAndCause_validMessageAndCause()
    {
        assertException(AgentRuntimeException.class, EXPECTED_MSG, NullPointerException.class,
                Exceptions.agentRuntime(new NullPointerException(), MSG_PATTERN, ARG1, ARG2));
    }

}
