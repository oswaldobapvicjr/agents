package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;

@Agent(type = AgentType.TIMER)
public class TestAgentWithNoNameAndTypeTimerAndNoRunMethod
{
    // INVALID: Missing @AgentTask method
}
