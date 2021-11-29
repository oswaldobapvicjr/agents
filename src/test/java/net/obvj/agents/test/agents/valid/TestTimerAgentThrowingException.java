package net.obvj.agents.test.agents.valid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

/**
 * An agent that throws an exception for testing purposes
 *
 * @author oswaldo.bapvic.jr
 */
@Agent(type = AgentType.TIMER, interval = "8760h", modulate = true) // 1 year
public class TestTimerAgentThrowingException
{
    @Run
    public void runTask()
    {
        throw new IllegalStateException("testing purpose");
    }

}
