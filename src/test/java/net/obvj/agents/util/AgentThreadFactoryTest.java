package net.obvj.agents.util;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import net.obvj.agents.util.AgentThreadFactory;

/**
 * Unit test methods for the {@link AgentThreadFactory} class.
 *
 * @author oswaldo.bapvic.jr
 */
class AgentThreadFactoryTest
{

    private static final String AGENT_A = "AgentA";
    private static final String AGENT_B = "AgentB";
    private static final String THREAD_PREFIX = "Agent-";

    private static final Runnable RUNNABLE = () -> {};

    // Test subject
    private AgentThreadFactory threadFactory1 = new AgentThreadFactory(AGENT_A);
    private AgentThreadFactory threadFactory2 = new AgentThreadFactory(AGENT_B);

    /**
     * Tests the threads are created with unique, sequential names
     */
    @Test
    void newThread_multipleAgentNames_validThreadNames()
    {
        Thread process1Thread1 = threadFactory1.newThread(RUNNABLE);
        Thread process1Thread2 = threadFactory1.newThread(RUNNABLE);
        Thread process2Thread1 = threadFactory2.newThread(RUNNABLE);
        Thread process2Thread2 = threadFactory2.newThread(RUNNABLE);

        assertThat(process1Thread1.getName(), is(THREAD_PREFIX + AGENT_A + "-thread1"));
        assertThat(process1Thread2.getName(), is(THREAD_PREFIX + AGENT_A + "-thread2"));
        assertThat(process2Thread1.getName(), is(THREAD_PREFIX + AGENT_B + "-thread1"));
        assertThat(process2Thread2.getName(), is(THREAD_PREFIX + AGENT_B + "-thread2"));
    }

    /**
     * Tests the threads created by the factory are not daemon
     */
    @Test
    void newThread_validRunnable_notDaemon()
    {
        Thread thread = threadFactory1.newThread(RUNNABLE);
        assertThat(thread.isDaemon(), is(false));
    }

    @Test
    void constructor_nullAgentName_exception()
    {
        assertThat(() -> new AgentThreadFactory(null), throwsException(IllegalArgumentException.class));
    }

}
