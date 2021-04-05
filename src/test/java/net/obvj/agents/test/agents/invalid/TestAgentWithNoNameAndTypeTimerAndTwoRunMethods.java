package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

@Agent(type = AgentType.TIMER)
public class TestAgentWithNoNameAndTypeTimerAndTwoRunMethods
{
    @Run
    public void timerTaskMethod()
    {
        System.out.println("timerTaskMethod() called");
    }

    // INVALID: only one method with @AgentTask allowed
    @Run
    public void timerTaskMethod2()
    {
        System.out.println("timerTaskMethod2() called");
    }
}
