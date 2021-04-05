package net.obvj.agents.conf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.obvj.agents.AgentType;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.test.agents.invalid.TestAgentWithAllCustomParams;
import net.obvj.agents.test.agents.invalid.TestAgentWithCustomNameAndType;
import net.obvj.agents.test.agents.invalid.TestAgentWithNoNameAndTypeTimerAndNoRunMethod;
import net.obvj.agents.test.agents.invalid.TestAgentWithNoType;

/**
 * Unit tests for the {@link AgentConfiguration}.
 *
 * @author oswaldo.bapvic.jr
 */
class AgentConfigurationTest
{
    private static final String NAME1 = "name1";
    private static final String INTERVAL = "90 seconds";

    @Test
    void fromAnnotatedClass_withAgentAnnotationNotPresent()
    {
        assertThrows(AgentConfigurationException.class, () -> AgentConfiguration.fromAnnotatedClass(Boolean.class));
    }

    @Test
    void fromAnnotatedClass_withAgentAnnotationWithNoType()
    {
        assertThat(AgentConfiguration.fromAnnotatedClass(TestAgentWithNoType.class).getType(), is(AgentType.TIMER));
    }

    @Test
    void fromAnnotatedClass_withAgentAnnotationPresentAndCustomTypeTimer()
    {
        Class<?> clazz = TestAgentWithNoNameAndTypeTimerAndNoRunMethod.class;
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(clazz);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.getName(), is(clazz.getCanonicalName()));
        assertThat(configuration.getType(), is(AgentType.TIMER));
        assertThat(configuration.getAgentClass(), is(clazz.getCanonicalName()));
        assertThat(configuration.getInterval(), is(AgentType.TIMER.getDefaultInterval()));
    }

    @Test
    void fromAnnotatedClass_withAgentAnnotationPresentAndCustomNameAndType()
    {
        Class<?> clazz = TestAgentWithCustomNameAndType.class;
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(clazz);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.getName(), is(NAME1));
        assertThat(configuration.getType(), is(AgentType.TIMER));
        assertThat(configuration.getAgentClass(), is(clazz.getCanonicalName()));
        assertThat(configuration.getInterval(), is(AgentType.TIMER.getDefaultInterval()));
    }

    @Test
    void fromAnnotatedClass_withAgentAnnotationAndAllCustomParams()
    {
        Class<?> clazz = TestAgentWithAllCustomParams.class;
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(clazz);

        assertThat(configuration.getName(), is(NAME1));
        assertThat(configuration.getType(), is(AgentType.TIMER));
        assertThat(configuration.getInterval(), is(INTERVAL));
        assertThat(configuration.getAgentClass(), is(clazz.getCanonicalName()));
    }

}
