package net.obvj.agents.conf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.*;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration.Builder;
import net.obvj.agents.conf.AgentConfiguration.Source;
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

    private static final AgentConfiguration AGENT_TIMER_DEFAULT = new AgentConfiguration.Builder(AgentType.TIMER)
            .className(CLASS_NAME1).name(NAME1).interval(INTERVAL1).build();

    private static final AgentConfiguration AGENT_TIMER_DEFAULT_CLONE = new AgentConfiguration.Builder(AgentType.TIMER)
            .className(CLASS_NAME1).name(NAME1).interval(INTERVAL1).build();

    private static final AgentConfiguration AGENT_TIMER_XML = new AgentConfiguration.Builder(AgentType.TIMER)
            .className(CLASS_NAME1).name(NAME1).interval(INTERVAL1).build(Source.XML);

    private static final AgentConfiguration AGENT_CRON_DEFAULT = new AgentConfiguration.Builder(AgentType.CRON)
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
    void getHighestPrecedenceSource_twoSources_highestPrecedenceSource()
    {
        // XML has higher precedence
        assertThat(Source.getHighestPrecedenceSource(Source.XML, Source.ANNOTATION), is(Source.XML));
    }

    @Test
    void getHighestPrecedenceSource_sinlgeSource_sameSource()
    {
        assertThat(Source.getHighestPrecedenceSource(Source.ANNOTATION), is(Source.ANNOTATION));
    }

    @Test
    void getHighestPrecedenceSource_noSource_default()
    {
        assertThat(Source.getHighestPrecedenceSource(), is(Source.DEFAULT));
    }

    @Test
    void getHighestPrecedenceConfiguration_twoDifferentSources_highestPrecedence()
    {
        // XML has higher precedence
        assertThat(AgentConfiguration.getHighestPrecedenceConfiguration(AGENT_TIMER_DEFAULT, AGENT_TIMER_XML), is(AGENT_TIMER_XML));
    }

    @Test
    void getHighestPrecedenceConfiguration_twoEqualSources_firstObject()
    {
        assertThat(AgentConfiguration.getHighestPrecedenceConfiguration(AGENT_TIMER_DEFAULT, AGENT_CRON_DEFAULT),
                is(AGENT_TIMER_DEFAULT));
    }

    @Test
    void getHighestPrecedenceConfiguration_singleObject_sameObject()
    {
        assertThat(AgentConfiguration.getHighestPrecedenceConfiguration(AGENT_CRON_DEFAULT), is(AGENT_CRON_DEFAULT));
    }

    @Test
    void getHighestPrecedenceConfiguration_noObject_exception()
    {
        assertThat(() -> AgentConfiguration.getHighestPrecedenceConfiguration(),
                throwsException(IllegalArgumentException.class));
    }

    @Test
    void getHighestPrecedenceConfiguration_nullObjectAndValidObject_validObject()
    {
        assertThat(AgentConfiguration.getHighestPrecedenceConfiguration((AgentConfiguration) null, AGENT_CRON_DEFAULT),
                is(AGENT_CRON_DEFAULT));
    }

    @Test
    void getHighestPrecedenceConfiguration_nullObject_exception()
    {
        assertThat(() -> AgentConfiguration.getHighestPrecedenceConfiguration((AgentConfiguration) null),
                throwsException(IllegalArgumentException.class));
    }

    @Test
    void getHighestPrecedenceConfiguration_nullObjects_exception()
    {
        assertThat(() -> AgentConfiguration.getHighestPrecedenceConfiguration((AgentConfiguration) null,
                (AgentConfiguration) null), throwsException(IllegalArgumentException.class));
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
        assertNotEquals(AGENT_TIMER_DEFAULT, AGENT_TIMER_XML);
        assertNotEquals(AGENT_TIMER_DEFAULT, AGENT_CRON_DEFAULT);
        assertNotEquals(AGENT_TIMER_DEFAULT, NAME1);
        assertNotEquals(AGENT_TIMER_DEFAULT, (AgentConfiguration) null);
    }

    @Test
    void hashCode_similarObjects_sameHash()
    {
        assertThat(Sets.newHashSet(AGENT_TIMER_DEFAULT, AGENT_TIMER_DEFAULT_CLONE).size(), is(1));
        assertThat(Sets.newHashSet(AGENT_TIMER_DEFAULT, AGENT_TIMER_DEFAULT_CLONE, AGENT_TIMER_XML).size(), is(2));
    }

    @Test
    void toString_allFieldsSet_allFieldsPresent()
    {
        String string = AGENT_TIMER_DEFAULT.toString().replaceAll("\"", "");
        assertThat(string, containsAll("name:name1", "className:className1", "type:TIMER", "interval:90 seconds",
                "source:DEFAULT"));
    }

    @Test
    void build_noClassName_exception()
    {
        assertThat(() -> new AgentConfiguration.Builder(AgentType.TIMER).build(),
                throwsException(AgentConfigurationException.class).withMessage(Builder.MSG_CLASS_NAME_CANNOT_BE_NULL));
    }

    @Test
    void build_nullType_exception()
    {
        assertThat(() -> new AgentConfiguration.Builder(null),
                throwsException(NullPointerException.class).withMessage(Builder.MSG_TYPE_CANNOT_BE_NULL));
    }

}
