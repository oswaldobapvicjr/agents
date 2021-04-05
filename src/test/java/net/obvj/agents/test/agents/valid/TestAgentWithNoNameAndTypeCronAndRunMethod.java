package net.obvj.agents.test.agents.valid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

@Agent(type = AgentType.CRON)
public class TestAgentWithNoNameAndTypeCronAndRunMethod
{
    @Run
    public void cronTaskMethod()
    {
        System.out.println("cronTaskMethod() called");
    }
}
