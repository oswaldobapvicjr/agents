package net.obvj.agents.test.agents.invalid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.agents.annotation.Run;

// INVALID: Not an @Agent
public class TestClassNotAgent
{
    private static final Logger LOG = LoggerFactory.getLogger(TestClassNotAgent.class);

    @Run
    public void run()
    {
        LOG.debug("TestClassNotAgent happily says: \"Hello!\"");
    }
}
