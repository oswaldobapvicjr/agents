package net.obvj.agents.impl;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.test.agents.invalid.TestAgentWithAllCustomParamsAndPrivateConstructor;
import net.obvj.agents.test.agents.invalid.TestAgentWithAllCustomParamsAndPrivateRunMethod;
import net.obvj.agents.test.agents.invalid.TestAgentWithNoNameAndTypeTimerAndNoRunMethod;
import net.obvj.agents.test.agents.invalid.TestAgentWithNoNameAndTypeTimerAndTwoRunMethods;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeTimerAndRunMethod;

/**
 * Unit tests for the {@link AnnotatedTimerAgent}.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class AnnotatedTimerAgentTest
{
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
    void initForClassWithoutAgentTaskAnnotation()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndNoRunMethod.class.getName());
        assertThat(() -> new AnnotatedTimerAgent(configuration), throwsException(AgentConfigurationException.class));
    }

    @Test
    void initForClassWithTwoAgentTaskAnnotations()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndTwoRunMethods.class.getName());
        assertThat(() -> new AnnotatedTimerAgent(configuration), throwsException(AgentConfigurationException.class));
    }

    @Test
    void runTaskForClassWithAgentTaskAnnotation()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndRunMethod.class.getName());
        AnnotatedTimerAgent annotatedTimerAgent = new AnnotatedTimerAgent(configuration);

        assertNotNull(annotatedTimerAgent.getMetadata().getAgentInstance());
        assertNotNull(annotatedTimerAgent.getMetadata().getRunMethod());

        annotatedTimerAgent.runTask();
    }

    @Test
    void initForClassWithAgentTaskAnnotationAndPrivateConstructor()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithAllCustomParamsAndPrivateConstructor.class.getName());
        assertThat(() -> new AnnotatedTimerAgent(configuration),
                throwsException(AgentConfigurationException.class).withCause(NoSuchMethodException.class));
    }

    @Test
    void runForClassWithAgentTaskAnnotationAndPrivateAgentTask()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithAllCustomParamsAndPrivateRunMethod.class.getName());
        assertThat(() -> new AnnotatedTimerAgent(configuration), throwsException(AgentConfigurationException.class));
    }

    @Test
    void toStringPrintsCustomString()
    {
        Mockito.when(configuration.getClassName())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndRunMethod.class.getName());
        AnnotatedTimerAgent annotatedTimerAgent = new AnnotatedTimerAgent(configuration);

        String string = annotatedTimerAgent.toString();
        assertEquals("AnnotatedTimerAgent$" + TestAgentWithNoNameAndTypeTimerAndRunMethod.class.getName(), string);
    }

}
