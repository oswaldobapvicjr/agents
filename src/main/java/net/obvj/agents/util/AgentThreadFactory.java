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

package net.obvj.agents.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

/**
 * An object that creates new threads for SMART agents.
 * <p>
 * The threads created by this factory are identified with the runnable agent name and
 * unique, sequential number.
 * <p>
 * The threads are also set up as non-daemon, to secure Process finalization before system
 * shutdown.
 *
 * @author oswaldo.bapvic.jr
 */
public class AgentThreadFactory implements ThreadFactory
{
    private static final String THREAD_NAME_FORMAT = "Agent-%s-thread%d";

    private final AtomicInteger nextSequenceNumber = new AtomicInteger(1);

    private String agentName;

    /**
     * Creates a new thread factory for the agent identified by the given name.
     *
     * @param agentName the agent name to compose new thread names; not null
     * @throws IllegalArgumentException if the specified name is either null or empty
     */
    public AgentThreadFactory(String agentName)
    {
        if (StringUtils.isEmpty(agentName))
        {
            throw Exceptions.illegalArgument("The agent name is mandatory");
        }
        this.agentName = agentName;
    }

    /**
     * Constructs a new {@link Thread}.
     *
     * @param runnable a {@link Runnable} to be executed by new thread instance
     * @return constructed thread, or null if the request to create a thread is rejected
     */
    @Override
    public Thread newThread(final Runnable runnable)
    {
        Thread thread = new Thread(runnable, newThreadName());
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setDaemon(false);
        return thread;
    }

    private String newThreadName()
    {
        return String.format(THREAD_NAME_FORMAT, agentName, nextSequenceNumber.getAndIncrement());
    }

}
