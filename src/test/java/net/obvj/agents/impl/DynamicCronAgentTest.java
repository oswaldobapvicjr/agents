package net.obvj.agents.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeCronAndRunMethod;

/**
 * Unit tests for the {@link DynamicCronAgent}.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class DynamicCronAgentTest
{
    private static final Class<?> VALID_CRON_AGENT_CLASS = TestAgentWithNoNameAndTypeCronAndRunMethod.class;

    @Mock
    AgentConfiguration configuration;

    @BeforeEach
    public void setup()
    {
        Mockito.when(configuration.getName()).thenReturn("name1");
        Mockito.when(configuration.getType()).thenReturn(AgentType.CRON);
        Mockito.when(configuration.getInterval()).thenReturn("* * * * *");
    }

    @Test
    void runTask_validAgent_noException()
    {
        Mockito.when(configuration.getClassName()).thenReturn(VALID_CRON_AGENT_CLASS.getName());
        DynamicCronAgent annotatedTimerAgent = new DynamicCronAgent(configuration);

        assertNotNull(annotatedTimerAgent.getMetadata().getAgentInstance());
        assertNotNull(annotatedTimerAgent.getMetadata().getRunMethod());

        annotatedTimerAgent.runTask();
    }

    @Test
    void toString_validAgent_customString()
    {
        Mockito.when(configuration.getClassName()).thenReturn(VALID_CRON_AGENT_CLASS.getName());
        DynamicCronAgent annotatedTimerAgent = new DynamicCronAgent(configuration);

        String string = annotatedTimerAgent.toString();
        assertThat(string, equalTo("AnnotatedCronAgent$" + VALID_CRON_AGENT_CLASS.getName()));
    }

}
