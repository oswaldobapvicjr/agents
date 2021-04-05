package net.obvj.agents.test.agents.valid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

@Agent(type = AgentType.TIMER, interval = "10 minutes")
public class TestTimerAgent1
{
    @Run
    public void timerTaskMethod()
    {
        System.out.println("timerTaskMethod() called");
    }
}
