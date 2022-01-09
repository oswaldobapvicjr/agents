/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.obvj.agents;

import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.EvictingQueue;

import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.conf.ConfigurationHolder;
import net.obvj.agents.conf.GlobalConfiguration;
import net.obvj.agents.util.ApplicationContextFacade;
import net.obvj.agents.util.DateUtils;
import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.DurationFormat;
import net.obvj.performetrics.util.DurationUtils;

/**
 * A common interface for all managed agents
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class AbstractAgent implements Runnable
{
    public enum State
    {
        SET, STARTED, RUNNING, STOPPED, ERROR;
    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAgent.class);

    protected static final String MSG_AGENT_ALREADY_STARTED = "Agent already started";
    protected static final String MSG_AGENT_ALREADY_STOPPED = "Agent already stopped";
    protected static final String MSG_AGENT_ALREADY_RUNNING = "Agent task already in execution";

    final AgentConfiguration configuration;

    private State previousState;
    private State currentState;

    /*
     * Stores the date & time this agent was started (schedule)
     */
    protected Date startDate;

    /*
     * The date & time when this agent task was last executed
     */
    protected Date lastRun;

    protected Duration lastRunDuration;

    /**
     * A history of most recent execution durations.
     */
    private final Queue<Duration> executionDurationHistory;

    /*
     * This object is used to control access to the task execution independently of other
     * operations.
     */
    private final Object runLock = new Object();
    private final Object changeLock = new Object();

    private boolean stopRequested = false;

    protected AbstractAgent(AgentConfiguration configuration)
    {
        this.configuration = configuration;
        this.executionDurationHistory = EvictingQueue.create(getMaxHistorySize());
    }

    /**
     * @return This agent's identifier name, as in {@link AgentConfiguration}.
     */
    public String getName()
    {
        return configuration.getName();
    }

    /**
     * @return This agent's type, as in {@link AgentConfiguration}.
     */
    public AgentType getType()
    {
        return configuration.getType();
    }

    /**
     * @return This agent's configuration data
     */
    public AgentConfiguration getConfiguration()
    {
        return configuration;
    }

    protected void setState(State currentState)
    {
        previousState = this.currentState;
        this.currentState = currentState;
    }

    /**
     * @return This agent's current state
     */
    public State getState()
    {
        return currentState;
    }

    /**
     * @return {@code true} if this agent's timer (not its task) is currently started;
     *         otherwise {@code false}.
     */
    public boolean isStarted()
    {
        return currentState == State.STARTED || (currentState == State.RUNNING && previousState == State.STARTED);
    }

    /**
     * @return {@code true} if this agent's task is currently running; otherwise
     *         {@code false}.
     */
    public boolean isRunning()
    {
        return currentState == State.RUNNING;
    }

    /**
     * @return {@code true} if this agent's timer is currently stopped; otherwise
     *         {@code false}.
     */
    public boolean isStopped()
    {
        return currentState == State.STOPPED;
    }

    /**
     * @return The date and time when this agent was started (scheduled).
     */
    public Date getStartDate()
    {
        return DateUtils.getClonedDate(startDate);
    }

    /**
     * @return The date and time when this agent task was last executed.
     */
    public Date getLastRunDate()
    {
        return DateUtils.getClonedDate(lastRun);
    }

    /**
     * Starts this agent timer considering the interval settled in this object for execution.
     */
    public final void start()
    {
        switch (getState())
        {
        case STARTED:
            throw new IllegalStateException(MSG_AGENT_ALREADY_STARTED);
        case STOPPED:
            throw new IllegalStateException("Agent was stopped. Please reset this agent before restarting");
        default:
            break;
        }
        synchronized (changeLock)
        {
            if (isStarted())
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_STARTED);
            }
            onStart();
            setState(State.STARTED);
            startDate = new Date();
        }
    }

    public abstract void onStart();

    /**
     * Suspends this agent.
     */
    public final void stop()
    {
        stopRequested = true;
        if (isStopped())
        {
            throw new IllegalStateException(MSG_AGENT_ALREADY_STOPPED);
        }
        synchronized (changeLock)
        {
            if (isStopped())
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_STOPPED);
            }
            LOG.info("Stopping agent: {}...", getName());
            onStop();
            setState(State.STOPPED);
            startDate = null;
            LOG.info("Agent {} stopped successfully.", getName());
        }
    }

    public abstract void onStop();

    /**
     * The method called by the system to execute the agent task automatically.
     */
    @Override
    public void run()
    {
        run(false);
    }

    public void run(boolean manualFlag)
    {
        if (stopRequested && !manualFlag) return;
        if (isRunning())
        {
            if (manualFlag)
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_RUNNING);
            }
            LOG.info(MSG_AGENT_ALREADY_RUNNING);
        }
        else
        {
            synchronized (runLock)
            {
                setState(State.RUNNING);
                lastRun = new Date();
                LOG.debug("Running agent...");
                try
                {
                    Stopwatch stopwatch = Stopwatch.createStarted(Counter.Type.WALL_CLOCK_TIME);
                    runTask();
                    updateStatistics(stopwatch.elapsedTime());
                    LOG.debug("Agent finished in {}", lastRunDuration);
                    afterRun();
                }
                catch (Exception exception)
                {
                    LOG.error("Agent finished with an exception", exception);
                }
                finally
                {
                    setState(previousState);
                }
            }
        }
    }

    private void updateStatistics(Duration duration)
    {
        this.lastRunDuration = duration;
        if (configuration.isEnableStatistics())
        {
            executionDurationHistory.offer(lastRunDuration);
        }
    }

    /**
     * Calculates and formats the average of last execution durations. Returns {@code "null"}
     * if no execution is available in the local history.
     *
     * @return a string containing the average of execution durations, or {@code "null"}
     */
    protected String formatAverageRunDuration()
    {
        if (configuration.isEnableStatistics())
        {
            Duration average = getAverageRunDuration();
            return formatDuration(average);
        }
        return "not enabled";
    }

    public Duration getAverageRunDuration()
    {
        return DurationUtils.average(new ArrayList<>(executionDurationHistory));
    }

    /**
     * Formats the last execution duration for reporting.
     *
     * @return the formatted duration, or {@code "null"}
     */
    protected String formatLastRunDuration()
    {
        return formatDuration(lastRunDuration);
    }

    /**
     * @param duration the {@link Duration} to be formatted
     * @return the formatted duration, or {@code "null"}
     */
    static String formatDuration(Duration duration)
    {
        return duration != null ? duration.toString(DurationFormat.SHORTER) : "null";
    }

    /**
     * Implements the logic for concrete agents. This method cannot be accessed externally.
     * Its functionality will be available via the run() method.
     */
    protected abstract void runTask();

    /**
     * An event to be fired after agent task run.
     */
    protected abstract void afterRun();

    /**
     * @return {@code true} if a stop request has been sent for this agent
     */
    protected boolean isStopRequested()
    {
        return stopRequested;
    }

    public abstract String getStatusString();

    protected ToStringBuilder getPresetStatusStringBuilder()
    {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        builder.append("name", getName())
               .append("type", getType())
               .append("status", getState())
               .append("startDate", (DateUtils.formatDate(startDate)))
               .append("lastExecutionStartDate", (DateUtils.formatDate(lastRun)))
               .append("lastExecutionDuration", formatLastRunDuration())
               .append("averageExecutionDuration", formatAverageRunDuration());
        return builder;
    }

    /**
     * @return the configured maximum number of durations in the history for statistics, or
     *         zero if statistics not enabled for this agent
     * @since 0.3.0
     */
    private int getMaxHistorySize()
    {
        if (configuration.isEnableStatistics())
        {
            int maxAgentHistorySize = getGlobalConfiguration().getMaxAgentHistorySize();
            return NumberUtils.max(maxAgentHistorySize, 0); // never a negative number
        }
        return 0;
    }

    private GlobalConfiguration getGlobalConfiguration()
    {
        return ApplicationContextFacade.getBean(ConfigurationHolder.class).getGlobalConfiguration();
    }

}
