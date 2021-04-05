package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

@Agent(name = "name1", type = AgentType.TIMER, interval = "90 seconds")
public class TestAgentWithAllCustomParamsAndPrivateConstructor
{
    // INVALID: Agent constructor should not be private
    private TestAgentWithAllCustomParamsAndPrivateConstructor()
    {
    }

    @Run
    public void agentTask()
    {
    }
}
