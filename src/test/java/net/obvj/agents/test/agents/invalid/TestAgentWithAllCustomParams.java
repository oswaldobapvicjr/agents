package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;

@Agent(name = "name1", type = AgentType.TIMER, frequency = "90 seconds")
public class TestAgentWithAllCustomParams
{
    // INVALID: Missing @Run method
}
