package net.obvj.agents;

import static net.obvj.agents.AgentType.CRON;
import static net.obvj.agents.AgentType.TIMER;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.obvj.agents.AbstractAgent.State;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.impl.DynamicTimerAgent;
import net.obvj.agents.test.agents.valid.DummyAgent;
import net.obvj.agents.test.agents.valid.TestTimerAgentThrowingException;
import net.obvj.agents.util.AgentFactory;
import net.obvj.agents.util.TimeInterval;

/**
 * Unit tests for the {@link TimerAgent} class.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class TimerAgentTest
{
    private static final String AGENT_CLASS_NAME = DummyAgent.class.getCanonicalName();
    private static final String DUMMY_AGENT = "DummyAgent";

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder().type(TIMER)
            .name(DUMMY_AGENT).className(AGENT_CLASS_NAME).interval("30 seconds").modulate(true).enableStatistics(true)
            .build();

    private static final AgentConfiguration DUMMY_AGENT_CONFIG_DISABLE_STATS = new AgentConfiguration.Builder()
            .type(TIMER).name(DUMMY_AGENT).className(AGENT_CLASS_NAME).interval("30 seconds").modulate(true)
            .enableStatistics(false).build();

    private static final AgentConfiguration DUMMY_AGENT_CONFIG_EVERY_DAY = new AgentConfiguration.Builder().type(TIMER)
            .name(DUMMY_AGENT).className(AGENT_CLASS_NAME).interval("24 hours").modulate(true).enableStatistics(true)
            .build();

    private static final AgentConfiguration TEST_CRON_AGENT_CONFIG = new AgentConfiguration.Builder().type(CRON)
            .name(DUMMY_AGENT).className(AGENT_CLASS_NAME).enableStatistics(true).build();

    @Mock
    private AgentConfiguration config;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private TimerAgent agentMock;

    /**
     * Tests that a non-timer agent will not be parsed by this class
     */
    @Test
    void constructor_otherAgentType_illegalArgument() throws ReflectiveOperationException
    {
        assertThat(() -> new DynamicTimerAgent(TEST_CRON_AGENT_CONFIG),
                throwsException(IllegalArgumentException.class).withMessageContaining("Not a timer agent"));
    }

    /**
     * Tests that no action is taken when start() is called on a started agent.
     */
    @Test
    void start_alreadyStared_illegalState()
    {
        when(agentMock.getState()).thenReturn(State.STARTED);
        assertThat(() -> agentMock.start(), throwsException(IllegalStateException.class)
                .withMessageContaining(TimerAgent.MSG_AGENT_ALREADY_STARTED));
    }

    /**
     * Tests that no action is taken when start() is called on a stopped agent.
     */
    @Test
    void start_stopped_illegalState()
    {
        when(agentMock.getState()).thenReturn(State.STOPPED);
        assertThat(() -> agentMock.start(), throwsException(IllegalStateException.class));
    }

    /**
     * Tests that no action is taken when stop() is called on a stoppedAgent.
     */
    @Test
    void stop_alreadyStopped_illegalState()
    {
        when(agentMock.isStopped()).thenReturn(true);
        assertThat(() -> agentMock.stop(), throwsException(IllegalStateException.class)
                .withMessageContaining(TimerAgent.MSG_AGENT_ALREADY_STOPPED));
    }

    @Test
    void stop_validAgent_executorServiceShutdown() throws TimeoutException
    {
        TimerAgent agent = spy((TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG_EVERY_DAY));
        agent.start();
        assertThat(agent.getExecutorService().isShutdown(), is(false));
        agent.stop();
        assertThat(agent.getExecutorService().isShutdown(), is(true));
    }

    /**
     * Tests that no action is taken when run() is called on a running agent.
     */
    @Test
    void run_alreadyRunning_noAction()
    {
        when(agentMock.isRunning()).thenReturn(true);
        agentMock.run();
        verify(agentMock, never()).runTask();
    }

    /**
     * Tests that exception is thrown when run(true) is called on a running agent.
     */
    @Test
    void run_alreadyRunningAndmanualFlagSet_illegalState()
    {
        when(agentMock.isRunning()).thenReturn(true);
        assertThat(() -> agentMock.run(true), throwsException(IllegalStateException.class)
                .withMessageContaining(TimerAgent.MSG_AGENT_ALREADY_RUNNING));
    }

    @Test
    void run_previousStateSet_previousStateRestoredAfterRun()
    {
        TimerAgent agent = spy((TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG));
        assertThat("State before run() should be SET", agent.getState(), is(State.SET));
        agent.run();
        verify(agent).runTask();
        assertThat("State after start() should be SET", agent.getState(), is(State.SET));
        assertThat(agent.getLastRunDate(), is(notNullValue()));
    }

    @Test
    void run_exception_executorServiceNotAffected()
    {
        AgentConfiguration exceptionThrowingAgent = AgentConfiguration
                .fromAnnotatedClass(TestTimerAgentThrowingException.class);
        TimerAgent agent = spy((TimerAgent) AgentFactory.create(exceptionThrowingAgent));
        assertThat("State before run() should be SET", agent.getState(), is(State.SET));
        agent.run();
        verify(agent).runTask();
        assertThat("State after start() should be SET", agent.getState(), is(State.SET));
        assertThat(agent.getLastRunDate(), is(notNullValue()));
        assertThat(agent.getExecutorService().isShutdown(), is(false));
        assertThat(agent.getExecutorService().isTerminated(), is(false));
    }

    @Test
    void getStatusString_validAgentWithStatusDisabled_validString()
    {
        TimerAgent agent = (TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG_DISABLE_STATS);
        String statusWithoutQuotes = agent.getStatusString().replace("\"", "");
        assertThat(statusWithoutQuotes,
                containsAll("name:DummyAgent", "type:TIMER", "status:SET", "startDate:null",
                        "lastExecutionStartDate:null", "interval:30 second(s)", "lastExecutionDuration:null",
                        "averageExecutionDuration:not enabled"));
    }

    @Test
    void getStatusString_validAgentWithStatsEnabled_validString()
    {
        TimerAgent agent = (TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        String statusWithoutQuotes = agent.getStatusString().replace("\"", "");
        assertThat(statusWithoutQuotes,
                containsAll("name:DummyAgent", "type:TIMER", "status:SET", "startDate:null",
                        "lastExecutionStartDate:null", "interval:30 second(s)", "lastExecutionDuration:null",
                        "averageExecutionDuration:0 second(s)"));
    }

    @Test
    void getInterval_validAgent_validInterval()
    {
        TimerAgent agent = (TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        assertThat(agent.getInterval(), is(equalTo(TimeInterval.of("30 seconds"))));
    }

    @Test
    void getInitialDelay_modulateFalse_zero()
    {
        when(agentMock.getConfiguration()).thenReturn(config);
        when(config.isModulate()).thenReturn(false);
        assertThat(agentMock.getInitialDelay(), is(equalTo(0L)));
    }

}
