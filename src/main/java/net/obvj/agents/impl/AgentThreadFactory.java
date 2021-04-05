package net.obvj.agents.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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
     * Creates a new thread factory for the agent identified by the given name
     *
     * @param agentName the agent name to compose new thread names
     */
    public AgentThreadFactory(String agentName)
    {
        this.agentName = agentName;
    }

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
