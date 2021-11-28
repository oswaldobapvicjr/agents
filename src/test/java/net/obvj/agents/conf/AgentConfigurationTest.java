package net.obvj.agents.conf;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration.Builder;
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
    private static final String CLASS_NAME1 = "className1";
    private static final String INTERVAL1 = "90 seconds";

    private static final AgentConfiguration AGENT_TIMER_DEFAULT = new AgentConfiguration.Builder().type(AgentType.TIMER)
            .className(CLASS_NAME1).name(NAME1).interval(INTERVAL1).build();

    private static final AgentConfiguration AGENT_TIMER_DEFAULT_CLONE = new AgentConfiguration.Builder()
            .type(AgentType.TIMER)
            .className(CLASS_NAME1).name(NAME1).interval(INTERVAL1).build();

    private static final AgentConfiguration AGENT_TIMER_XML = new AgentConfiguration.Builder().type(AgentType.TIMER)
            .className(CLASS_NAME1).name(NAME1).interval(INTERVAL1).build();

    private static final AgentConfiguration AGENT_CRON_DEFAULT = new AgentConfiguration.Builder().type(AgentType.CRON)
            .className(CLASS_NAME1).build();

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
        assertThat(configuration.getClassName(), is(clazz.getCanonicalName()));
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
        assertThat(configuration.getClassName(), is(clazz.getCanonicalName()));
        assertThat(configuration.getInterval(), is(AgentType.TIMER.getDefaultInterval()));
    }

    @Test
    void fromAnnotatedClass_withAgentAnnotationAndAllCustomParams()
    {
        Class<?> clazz = TestAgentWithAllCustomParams.class;
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(clazz);

        assertThat(configuration.getName(), is(NAME1));
        assertThat(configuration.getType(), is(AgentType.TIMER));
        assertThat(configuration.getInterval(), is(INTERVAL1));
        assertThat(configuration.getClassName(), is(clazz.getCanonicalName()));
    }

    @Test
    void equals_similarObjects_true()
    {
        assertNotSame(AGENT_TIMER_DEFAULT, AGENT_TIMER_DEFAULT_CLONE);
        assertEquals(AGENT_TIMER_DEFAULT, AGENT_TIMER_DEFAULT_CLONE);

        assertSame(AGENT_TIMER_DEFAULT, AGENT_TIMER_DEFAULT);
        assertEquals(AGENT_TIMER_DEFAULT, AGENT_TIMER_DEFAULT);
    }

    @Test
    void equals_notSimilarObjects_false()
    {
        assertNotEquals(AGENT_TIMER_DEFAULT, AGENT_CRON_DEFAULT);
        assertNotEquals(AGENT_TIMER_DEFAULT, NAME1);
        assertNotEquals(AGENT_TIMER_DEFAULT, (AgentConfiguration) null);
    }

    @Test
    void hashCode_similarObjects_sameHash()
    {
        assertThat(Sets.newHashSet(AGENT_TIMER_DEFAULT, AGENT_TIMER_DEFAULT_CLONE).size(), is(1));
    }

    @Test
    void toString_allFieldsSet_allFieldsPresent()
    {
        String string = AGENT_TIMER_DEFAULT.toString().replaceAll("\"", "");
        assertThat(string, containsAll("name:name1", "className:className1", "type:TIMER", "interval:90 seconds"));
    }

    @Test
    void build_noClassName_exception()
    {
        assertThat(() -> new AgentConfiguration.Builder().type(AgentType.TIMER).build(),
                throwsException(AgentConfigurationException.class).withMessage(Builder.MSG_CLASS_NAME_CANNOT_BE_NULL));
    }

    @Test
    void build_nullType_exception()
    {
        assertThat(() -> new AgentConfiguration.Builder().build(),
                throwsException(NullPointerException.class).withMessage(Builder.MSG_TYPE_CANNOT_BE_NULL));
    }

}
