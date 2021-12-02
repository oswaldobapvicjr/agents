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

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.util.AgentThreadFactory;
import net.obvj.agents.util.DateUtils;
import net.obvj.agents.util.TimeInterval;

/**
 * A thread-safe extensible Agent for tasks that are scheduled in the system to run
 * repeatedly, given an interval that is particular to each task. Available operations
 * are: 'start', 'stop', 'run' and 'reset'
 *
 * @author oswaldo.bapvic.jr
 */
public abstract class TimerAgent extends AbstractAgent
{
    private static final Logger LOG = LoggerFactory.getLogger(TimerAgent.class);

    private TimeInterval interval;

    private AgentThreadFactory threadFactory;
    private ScheduledExecutorService schedule;

    /**
     * Builds a {@link TimerAgent} from the given configuration.
     *
     * @param configuration the {@link AgentConfiguration} to be set
     */
    protected TimerAgent(AgentConfiguration configuration)
    {
        super(configuration);

        if (configuration.getType() != AgentType.TIMER)
        {
            throw new IllegalArgumentException("Not a timer agent");
        }

        TimeInterval timeInterval = TimeInterval.of(configuration.getInterval());
        this.interval = timeInterval;

        threadFactory = new AgentThreadFactory(getName());
        schedule = Executors.newSingleThreadScheduledExecutor(threadFactory);

        setState(State.SET);
    }

    /**
     * Starts this agent timer considering the interval settled in this object for execution.
     */
    @Override
    public final void onStart()
    {
        LOG.info("Starting agent: {}", getName());
        LOG.info("Agent {} scheduled to run every {}.", getName(), interval);

        schedule.scheduleAtFixedRate(this, getInitialDelay(), interval.toMillis(),
                java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    protected long getInitialDelay()
    {
        if (super.getConfiguration().isModulate())
        {
            Date start = DateUtils.getNextExactDateEveryInterval(interval.getDuration(), interval.getTimeUnit());

            if (LOG.isInfoEnabled())
            {
                LOG.info("First execution of {} will be at: {}", getName(), DateUtils.formatDate(start));
            }

            return start.getTime() - System.currentTimeMillis();
        }
        return 0L;
    }

    /**
     * Terminates this agent timer gracefully. Does not interfere with a currently executing
     * task, if it exists.
     */
    @Override
    public final void onStop()
    {
        schedule.shutdown();
    }

    @Override
    public void afterRun()
    {
        // Nothing required after task
    }

    /**
     * Returns the interval configured for this {@code TimerAgent}.
     *
     * @return the interval configured for this {@code TimerAgent}.
     */
    public TimeInterval getInterval()
    {
        return interval;
    }

    /**
     * @return A string with current agent status in JSON format
     */
    @Override
    public String getStatusString()
    {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        builder.append("name", getName()).append("type", getType()).append("status", getState())
                .append("startDate", (DateUtils.formatDate(startDate)))
                .append("lastExecutionStartDate", (DateUtils.formatDate(lastRun)))
                .append("lastExecutionDuration", formatLastRunDuration())
                .append("averageExecutionDuration", formatAverageRunDuration())
                .append("interval", interval);
        return builder.build();
    }

    /**
     * Returns the {@link ExecutorService} associated with this agent instance, for testing
     * purposes.
     *
     * @return the {@link ExecutorService}
     */
    protected ScheduledExecutorService getExecutorService()
    {
        return schedule;
    }

}
