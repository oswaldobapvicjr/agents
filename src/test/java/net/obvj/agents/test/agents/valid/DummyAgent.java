package net.obvj.agents.test.agents.valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;

/**
 * A dummy agent that prints a dummy message for testing purposes
 *
 * @author oswaldo.bapvic.jr
 */
@Agent(type = AgentType.TIMER, interval = "5 seconds")
public class DummyAgent
{
    private static final Logger LOG = LoggerFactory.getLogger(DummyAgent.class);

    @Run
    public void runTask()
    {
        LOG.debug("DummyAgent says: \"Hello!\"");
    }

}
