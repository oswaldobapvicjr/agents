package net.obvj.agents;

import static net.obvj.agents.AgentType.CRON;
import static net.obvj.agents.AgentType.TIMER;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import net.obvj.agents.AbstractAgent.State;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.impl.AnnotatedCronAgent;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeCronAndRunMethod;
import net.obvj.agents.util.AgentFactory;
import net.obvj.agents.util.DateUtils;

/**
 * Unit tests for the {@link CronAgent} class.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class CronAgentTest
{
    static
    {
        Locale.setDefault(Locale.UK);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    private static final ZonedDateTime DATE_20_04_27T23_25_13 = ZonedDateTime.of(2020, 4, 27, 23, 25, 13, 123456789, DEFAULT_ZONE);
    private static final ZonedDateTime DATE_20_04_27T23_26_00 = ZonedDateTime.of(2020, 4, 27, 23, 26, 0, 000000123, DEFAULT_ZONE);
    private static final ZonedDateTime DATE_20_04_27T23_30_00 = ZonedDateTime.of(2020, 4, 27, 23, 30, 0, 000000123, DEFAULT_ZONE);
    private static final ZonedDateTime DATE_20_04_28T02_00_00 = ZonedDateTime.of(2020, 4, 28, 2, 0, 0, 000000234, DEFAULT_ZONE);
    private static final ZonedDateTime DATE_20_05_04T00_00_00 = ZonedDateTime.of(2020, 5, 2, 0, 0, 0, 000000234, DEFAULT_ZONE);

    private static final String STR_CRON_EVERY_MINUTE = "* * * * *";
    private static final String STR_CRON_EVERY_30_MIN = "/30 * * * *";
    private static final String STR_CRON_EVERY_DAY_AT_2_AM = "0 2 * * *";
    private static final String STR_CRON_HOURLY_ON_WEEKEND = "0 * * * SAT,SUN";

    private static final String AGENT_NAME = "Agent1";

    private static final String AGENT_CRON_CLASS_NAME = TestAgentWithNoNameAndTypeCronAndRunMethod.class
            .getCanonicalName();

    private static final AgentConfiguration AGENT_CFG_EVERY_MINUTE = new AgentConfiguration.Builder(CRON)
            .name(AGENT_NAME).className(AGENT_CRON_CLASS_NAME).interval(STR_CRON_EVERY_MINUTE).build();

    private static final AgentConfiguration AGENT_CFG_EVERY_30_MIN = new AgentConfiguration.Builder(CRON)
            .name(AGENT_NAME).className(AGENT_CRON_CLASS_NAME).interval(STR_CRON_EVERY_30_MIN).build();

    private static final AgentConfiguration AGENT_CFG_EVERY_DAY_AT_2_AM = new AgentConfiguration.Builder(CRON)
            .name(AGENT_NAME).className(AGENT_CRON_CLASS_NAME).interval(STR_CRON_EVERY_DAY_AT_2_AM).build();

    private static final AgentConfiguration AGENT_CFG_HOURLY_ON_WEEKEND = new AgentConfiguration.Builder(CRON)
            .name(AGENT_NAME).className(AGENT_CRON_CLASS_NAME).interval(STR_CRON_HOURLY_ON_WEEKEND).build();

    private static final CronAgent AGENT_EVERY_MINUTE = spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_MINUTE));
    private static final CronAgent AGENT_EVERY_30_MIN = spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_30_MIN));
    private static final CronAgent AGENT_EVERY_DAY_AT_2_AM = spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_DAY_AT_2_AM));
    private static final CronAgent AGENT_HOURLY_ON_WEEKEND = spy((CronAgent) AgentFactory.create(AGENT_CFG_HOURLY_ON_WEEKEND));

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder(CRON).name(AGENT_NAME)
            .className(AGENT_CRON_CLASS_NAME).interval("0 0 * * 0").build();

    private static final AgentConfiguration TEST_TIMER_AGENT_CONFIG = new AgentConfiguration.Builder(TIMER)
            .name(AGENT_NAME).className(AGENT_CRON_CLASS_NAME).build();

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private CronAgent agentMock;

    @BeforeEach
    void before()
    {
        when(AGENT_EVERY_MINUTE.isStarted()).thenReturn(true);
        when(AGENT_EVERY_30_MIN.isStarted()).thenReturn(true);
        when(AGENT_EVERY_DAY_AT_2_AM.isStarted()).thenReturn(true);
        when(AGENT_HOURLY_ON_WEEKEND.isStarted()).thenReturn(true);
    }

    /**
     * Asserts that two dates are equal, ignoring the nanoseconds part.
     *
     * @param expectedDate the expected date
     * @param actualDate   the actual date
     */
    private void assertEqualDatesIgnoringNanos(ZonedDateTime expectedDate, ZonedDateTime actualDate)
    {
        ZonedDateTime tmpExpectedDate = expectedDate.withNano(0);
        ZonedDateTime tmpActualDate = actualDate.withNano(0);
        assertThat(tmpActualDate, is(equalTo(tmpExpectedDate)));
    }

    /**
     * Tests that a non-cron agent will not be parsed by this class
     */
    @Test
    void constructor_otherAgentType_illegalArgument() throws ReflectiveOperationException
    {
        assertThat(() -> new AnnotatedCronAgent(TEST_TIMER_AGENT_CONFIG),
                throwsException(IllegalArgumentException.class).withMessageContaining("Not a cron agent"));
    }

    @Test
    void scheduleNextExecution_validAgents_validNextExecutionDates()
    {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class))
        {
            dateUtils.when(DateUtils::now).thenReturn(DATE_20_04_27T23_25_13);
            AGENT_EVERY_MINUTE.scheduleNextExecution();
            AGENT_EVERY_30_MIN.scheduleNextExecution();
            AGENT_EVERY_DAY_AT_2_AM.scheduleNextExecution();
            AGENT_HOURLY_ON_WEEKEND.scheduleNextExecution();
        }

        assertEqualDatesIgnoringNanos(DATE_20_04_27T23_26_00, AGENT_EVERY_MINUTE.getNextExecutionDate().get());
        assertEqualDatesIgnoringNanos(DATE_20_04_27T23_30_00, AGENT_EVERY_30_MIN.getNextExecutionDate().get());
        assertEqualDatesIgnoringNanos(DATE_20_04_28T02_00_00, AGENT_EVERY_DAY_AT_2_AM.getNextExecutionDate().get());
        assertEqualDatesIgnoringNanos(DATE_20_05_04T00_00_00, AGENT_HOURLY_ON_WEEKEND.getNextExecutionDate().get());
    }

    @Test
    void start_validAgent_validNextExecutionDate()
    {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class))
        {
            dateUtils.when(DateUtils::now).thenReturn(DATE_20_04_27T23_25_13);
            when(AGENT_EVERY_DAY_AT_2_AM.isStarted()).thenReturn(false);
            AGENT_EVERY_DAY_AT_2_AM.start();
        }

        assertEqualDatesIgnoringNanos(DATE_20_04_28T02_00_00, AGENT_EVERY_DAY_AT_2_AM.getNextExecutionDate().get());
    }

    @Test
    void run_validAgent_validNextExecutionAfterRun()
    {
        try (MockedStatic<DateUtils> dateUtils = mockStatic(DateUtils.class))
        {
            dateUtils.when(DateUtils::now).thenReturn(DATE_20_04_27T23_25_13);
            AGENT_EVERY_DAY_AT_2_AM.run();
        }

        assertEqualDatesIgnoringNanos(DATE_20_04_28T02_00_00, AGENT_EVERY_DAY_AT_2_AM.getNextExecutionDate().get());
    }

    @Test
    void stop_validAgent_nextExecutionDateEmptyAndExecutorServiceShutdown() throws TimeoutException
    {
        CronAgent agent = spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_DAY_AT_2_AM));
        agent.start();
        assertThat(agent.getExecutorService().isShutdown(), is(false));
        assertThat(agent.getNextExecutionDate().get(), is(notNullValue()));
        agent.stop();
        assertThat(agent.getExecutorService().isShutdown(), is(true));
        assertThat(agent.getNextExecutionDate(), is(Optional.empty()));
    }

    /**
     * Tests that no action is taken when start() is called on a started agent.
     */
    @Test
    void start_alreadyStarted_illegalState()
    {
        when(agentMock.getState()).thenReturn(State.STARTED);
        assertThat(() -> agentMock.start(), throwsException(IllegalStateException.class)
                .withMessageContaining(CronAgent.MSG_AGENT_ALREADY_STARTED));
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
     * Tests that exception thrown when stop() is called on a stopped agent.
     */
    @Test
    void stop_alreadyStopped_illegalState()
    {
        when(agentMock.isStopped()).thenReturn(true);
        assertThat(() -> agentMock.stop(), throwsException(IllegalStateException.class)
                .withMessageContaining(CronAgent.MSG_AGENT_ALREADY_STOPPED));
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
     * Tests that an exception is thrown when run(true) is called on a running agent.
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
        CronAgent agent = spy((CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG));
        assertThat("State before run() should be SET", agent.getState(), is(State.SET));
        agent.run();
        verify(agent).runTask();
        assertThat("State after start() should be SET", agent.getState(), is(State.SET));
        assertThat(agent.getLastRunDate(), is(notNullValue()));
    }

    @Test
    void getAgentStatusString_validAgent_validString()
    {
        CronAgent agent = (CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        String statusWithoutQuotes = agent.getStatusString().replace("\"", "");
        assertThat(statusWithoutQuotes,
                containsAll("name:Agent1", "type:CRON", "status:SET", "startDate:null", "lastExecutionStartDate:null",
                        "cronExpression:0 0 * * 0", "cronDescription", "nextExecutionDate",
                        "lastExecutionDuration:null", "averageExecutionDuration:0 second(s)"));
    }

    @Test
    void getCronExpression_validAgent_validCronExpression()
    {
        CronAgent agent = (CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        assertThat(agent.getCronExpression(), is(equalTo("0 0 * * 0")));
    }

}
