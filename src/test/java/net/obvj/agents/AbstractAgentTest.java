package net.obvj.agents;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.obvj.agents.AbstractAgent.State;
import net.obvj.performetrics.util.Duration;

/**
 * Unit tests for the {@link AbstractAgent} class.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class AbstractAgentTest
{
    // The setting CALLS_REAL_METHODS allows mocking abstract methods/classes
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    AbstractAgent agent;

    @Test
    void isStarted_set_false()
    {
        agent.setState(State.SET);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
    }

    @Test
    void isRunning_set_false()
    {
        agent.setState(State.SET);
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
    }

    @Test
    void isStopped_set_false()
    {
        agent.setState(State.SET);
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }

    @Test
    void isStarted_started_true()
    {
        agent.setState(State.STARTED);
        assertTrue("expected true on agent.isStarted()", agent.isStarted());
    }

    @Test
    void isRunning_started_false()
    {
        agent.setState(State.STARTED);
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
    }

    @Test
    void isStopped_started_false()
    {
        agent.setState(State.STARTED);
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }

    @Test
    void isStarted_running_false()
    {
        agent.setState(State.RUNNING);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
    }

    @Test
    void isRunning_running_true()
    {
        agent.setState(State.RUNNING);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
        assertTrue("expected true on agent.isRunning()", agent.isRunning());
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }

    @Test
    void isStopped_running_false()
    {
        agent.setState(State.RUNNING);
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }

    @Test
    void isStarted_stopped_false()
    {
        agent.setState(State.STOPPED);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
    }

    @Test
    void isRunning_stopped_false()
    {
        agent.setState(State.STOPPED);
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
    }

    @Test
    void isStopped_stopped_true()
    {
        agent.setState(State.STOPPED);
        assertTrue("expected true on agent.isStopped()", agent.isStopped());
    }

    @Test
    void getStartDate_validCalendar_calendarClone()
    {
        Calendar now = Calendar.getInstance();
        agent.startDate = now;
        Calendar startDate = agent.getStartDate();
        assertNotSame(now, startDate);
        assertThat(startDate.getTime(), is(now.getTime()));
    }

    @Test
    void getStartDate_null_null()
    {
        agent.startDate = null;
        Calendar lastRunDate = agent.getStartDate();
        assertNull(lastRunDate);
    }

    @Test
    void getLastRunDate_validCalendar_calendarClone()
    {
        Calendar now = Calendar.getInstance();
        agent.lastRun = now;
        Calendar lastRunDate = agent.getLastRunDate();
        assertNotSame(now, lastRunDate);
        assertThat(lastRunDate.getTime(), is(now.getTime()));
    }

    @Test
    void getLastRunDate_null_null()
    {
        agent.lastRun = null;
        Calendar lastRunDate = agent.getLastRunDate();
        assertNull(lastRunDate);
    }

    @Test
    void formatDuration_null_null()
    {
        assertThat(AbstractAgent.formatDuration(null), is("null"));
    }

    @Test
    void formatDuration_valid_short()
    {
        assertThat(AbstractAgent.formatDuration(Duration.of(50, TimeUnit.SECONDS)), is("50.000000000 second(s)"));
    }

}
