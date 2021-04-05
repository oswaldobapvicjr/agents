package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

@Agent(name = "name1", type = AgentType.TIMER)
public class TestAgentWithCustomNameAndType
{
    @Run
    public void run(String arg1)
    {
        // INVALID: @Run method should have no parameter
    }
}
