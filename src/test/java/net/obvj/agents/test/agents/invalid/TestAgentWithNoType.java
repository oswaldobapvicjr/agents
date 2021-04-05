package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.annotation.Agent;

// No type set; "timer" will be considered
@Agent()
public class TestAgentWithNoType
{
    // INVALID: Missing @AgentTask method
}
