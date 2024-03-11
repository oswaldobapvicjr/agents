package net.obvj.agents;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
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
        assertFalse(agent.isStarted());
    }

    @Test
    void isRunning_set_false()
    {
        agent.setState(State.SET);
        assertFalse(agent.isRunning());
    }

    @Test
    void isStopped_set_false()
    {
        agent.setState(State.SET);
        assertFalse(agent.isStopped());
    }

    @Test
    void isStarted_started_true()
    {
        agent.setState(State.STARTED);
        assertTrue(agent.isStarted());
    }

    @Test
    void isRunning_started_false()
    {
        agent.setState(State.STARTED);
        assertFalse(agent.isRunning());
    }

    @Test
    void isStopped_started_false()
    {
        agent.setState(State.STARTED);
        assertFalse(agent.isStopped());
    }

    @Test
    void isStarted_running_false()
    {
        agent.setState(State.RUNNING);
        assertFalse(agent.isStarted());
    }

    @Test
    void isRunning_running_true()
    {
        agent.setState(State.RUNNING);
        assertFalse(agent.isStarted(), () -> "expected false on agent.isStarted()");
        assertTrue(agent.isRunning(), () -> "expected true on agent.isRunning()");
        assertFalse(agent.isStopped(), () -> "expected false on agent.isStopped()");
    }

    @Test
    void isStopped_running_false()
    {
        agent.setState(State.RUNNING);
        assertFalse(agent.isStopped());
    }

    @Test
    void isStarted_stopped_false()
    {
        agent.setState(State.STOPPED);
        assertFalse(agent.isStarted());
    }

    @Test
    void isRunning_stopped_false()
    {
        agent.setState(State.STOPPED);
        assertFalse(agent.isRunning());
    }

    @Test
    void isStopped_stopped_true()
    {
        agent.setState(State.STOPPED);
        assertTrue(agent.isStopped());
    }

    @Test
    void getStartDate_validCalendar_calendarClone()
    {
        Date now = new Date();
        agent.startDate = now;
        Date startDate = agent.getStartDate();
        assertNotSame(now, startDate);
        assertThat(startDate.getTime(), is(now.getTime()));
    }

    @Test
    void getStartDate_null_null()
    {
        agent.startDate = null;
        Date lastRunDate = agent.getStartDate();
        assertNull(lastRunDate);
    }

    @Test
    void getLastRunDate_validCalendar_calendarClone()
    {
        Date now = new Date();
        agent.lastRun = now;
        Date lastRunDate = agent.getLastRunDate();
        assertNotSame(now, lastRunDate);
        assertThat(lastRunDate.getTime(), is(now.getTime()));
    }

    @Test
    void getLastRunDate_null_null()
    {
        agent.lastRun = null;
        Date lastRunDate = agent.getLastRunDate();
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
        assertThat(AbstractAgent.formatDuration(Duration.of(50, TimeUnit.SECONDS)), is("50 second(s)"));
    }

}
