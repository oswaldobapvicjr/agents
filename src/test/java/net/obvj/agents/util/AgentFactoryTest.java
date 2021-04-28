package net.obvj.agents.util;

import static net.obvj.agents.AgentType.CRON;
import static net.obvj.agents.AgentType.TIMER;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import net.obvj.agents.CronAgent;
import net.obvj.agents.TimerAgent;
import net.obvj.agents.AbstractAgent.State;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.impl.AnnotatedCronAgent;
import net.obvj.agents.impl.AnnotatedTimerAgent;
import net.obvj.agents.test.agents.valid.DummyAgent;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeTimerAndRunMethod;
import net.obvj.agents.util.AgentFactory;
import net.obvj.agents.util.TimeUnit;

/**
 * Unit tests for the {@link AgentFactory}.
 *
 * @author oswado.bapvic.jr
 */
class AgentFactoryTest
{
    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.agents.test.agents.valid.DummyAgent";

    private static final Class<?> CRON_TEST_AGENT_CLASS = TestAgentWithNoNameAndTypeTimerAndRunMethod.class;

    private static final String INTERVAL_CRON_EVERY_TWO_MINUTES = "*/2 * * * *";
    private static final String INTERVAL_TIMER_30_SECONDS = "30 seconds";

    @Test
    void constuctor_instantiationNotAllowed()
    {
        assertThat(AgentFactory.class, instantiationNotAllowed());
    }

    @Test
    void create_timerAgent30Seconds()
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(TIMER).name(DUMMY_AGENT)
                .className(DUMMY_AGENT_CLASS).interval(INTERVAL_TIMER_30_SECONDS).build();

        TimerAgent timerAgent = (TimerAgent) AgentFactory.create(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(AnnotatedTimerAgent.class)));
        assertThat(timerAgent.getConfiguration().getClassName(), is(DummyAgent.class.getName()));

        assertThat(timerAgent.getConfiguration(), is(configuration));

        assertThat(timerAgent.getInterval().getDuration(), is(30));
        assertThat(timerAgent.getInterval().getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    void create_timerAgentDefaultValues()
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(TIMER).name(DUMMY_AGENT)
                .className(DUMMY_AGENT_CLASS).build();

        TimerAgent timerAgent = (TimerAgent) AgentFactory.create(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(AnnotatedTimerAgent.class)));
        assertThat(timerAgent.getConfiguration().getClassName(), is(DummyAgent.class.getName()));
        assertThat(timerAgent.getConfiguration(), is(configuration));

        assertThat(timerAgent.getInterval().getDuration(), is(1));
        assertThat(timerAgent.getInterval().getTimeUnit(), is(TimeUnit.MINUTES));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    void create_annotatedTimerAgent()
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(TIMER).name(DUMMY_AGENT)
                .className(CRON_TEST_AGENT_CLASS.getName()).interval(INTERVAL_TIMER_30_SECONDS).build();

        TimerAgent timerAgent = (TimerAgent) AgentFactory.create(configuration);
        assertThat(timerAgent, instanceOf(AnnotatedTimerAgent.class));

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getConfiguration(), is(configuration));

        assertThat(timerAgent.getInterval().getDuration(), is(30));
        assertThat(timerAgent.getInterval().getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    void create_annotatedCronAgent()
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(CRON).name(DUMMY_AGENT)
                .className(CRON_TEST_AGENT_CLASS.getName()).interval(INTERVAL_CRON_EVERY_TWO_MINUTES).build();

        CronAgent cronAgent = (CronAgent) AgentFactory.create(configuration);
        assertThat(cronAgent, instanceOf(AnnotatedCronAgent.class));

        assertThat(cronAgent.getName(), is(DUMMY_AGENT));
        assertThat(cronAgent.getType(), is(CRON));
        assertThat(cronAgent.getConfiguration(), is(configuration));

        assertThat(cronAgent.getCronExpression(), is(INTERVAL_CRON_EVERY_TWO_MINUTES));

        assertThat(cronAgent.getState(), is(State.SET));
        assertThat(cronAgent.isStarted(), is(false));
    }

}
