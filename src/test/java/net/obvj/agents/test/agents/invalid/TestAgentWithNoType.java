package net.obvj.agents.test.agents.invalid;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

// No type set; "timer" will be considered
@Agent()
public class TestAgentWithNoType
{
    @Run
    private void timeout()
    {
        // INVALID: The @Run method should be public
    }
}
