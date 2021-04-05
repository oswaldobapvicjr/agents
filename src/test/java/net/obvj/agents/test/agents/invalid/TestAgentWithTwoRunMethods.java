package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

@Agent(name = "name1", type = AgentType.TIMER, interval = "90 seconds")
public class TestAgentWithTwoRunMethods
{
    @Run
    public void run1()
    {
        // Dummy
    }

    @Run
    public void run2()
    {
        // INVALID: Only one public method annotated with @Run allowed
    }
}
