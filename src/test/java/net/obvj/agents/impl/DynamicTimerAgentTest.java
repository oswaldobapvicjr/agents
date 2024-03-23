package net.obvj.agents.impl;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.InvalidClassException;
import net.obvj.agents.test.agents.invalid.TestAgentWithAllCustomParamsAndPrivateConstructor;
import net.obvj.agents.test.agents.invalid.TestAgentWithAllCustomParamsAndPrivateRunMethod;
import net.obvj.agents.test.agents.invalid.TestAgentWithNoNameAndTypeTimerAndNoRunMethod;
import net.obvj.agents.test.agents.invalid.TestAgentWithNoNameAndTypeTimerAndTwoRunMethods;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeTimerAndRunMethod;

/**
 * Unit tests for the {@link DynamicTimerAgent}.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class DynamicTimerAgentTest
{
    private static final Class<?> VALID_AGENT_CLASS = TestAgentWithNoNameAndTypeTimerAndRunMethod.class;

    @Mock
    AgentConfiguration configuration;

    @BeforeEach
    public void setup()
    {
        Mockito.when(configuration.getName()).thenReturn("name1");
        Mockito.when(configuration.getType()).thenReturn(AgentType.TIMER);
        Mockito.when(configuration.getInterval()).thenReturn("60 seconds");
    }

    @Test
    void constructor_classWithoutRunMethod_exception()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndNoRunMethod.class.getName());
        assertThat(() -> new DynamicTimerAgent(configuration), throwsException(InvalidClassException.class));
    }

    @Test
    void constructor_classWithTowRunMethods_exception()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndTwoRunMethods.class.getName());
        assertThat(() -> new DynamicTimerAgent(configuration), throwsException(InvalidClassException.class));
    }

    @Test
    void runTask_validClass_noException()
    {
        Mockito.when(configuration.getClassName()).thenReturn(VALID_AGENT_CLASS.getName());
        DynamicTimerAgent annotatedTimerAgent = new DynamicTimerAgent(configuration);

        assertNotNull(annotatedTimerAgent.getMetadata().getAgentInstance());
        assertNotNull(annotatedTimerAgent.getMetadata().getRunMethod());

        annotatedTimerAgent.runTask();
    }

    @Test
    void constructor_agentClassWithPrivateConstructor_exception()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithAllCustomParamsAndPrivateConstructor.class.getName());
        assertThat(() -> new DynamicTimerAgent(configuration),
                throwsException(InvalidClassException.class).withCause(NoSuchMethodException.class));
    }

    @Test
    void constructor_agentClassWithAgentPrivateRunTask_exception()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithAllCustomParamsAndPrivateRunMethod.class.getName());
        assertThat(() -> new DynamicTimerAgent(configuration), throwsException(InvalidClassException.class));
    }

    @Test
    void toString_validAgent_customString()
    {
        Mockito.when(configuration.getClassName()).thenReturn(VALID_AGENT_CLASS.getName());
        DynamicTimerAgent annotatedTimerAgent = new DynamicTimerAgent(configuration);

        String string = annotatedTimerAgent.toString();
        assertThat(string, equalTo("AnnotatedTimerAgent$" + VALID_AGENT_CLASS.getName()));
    }

}
