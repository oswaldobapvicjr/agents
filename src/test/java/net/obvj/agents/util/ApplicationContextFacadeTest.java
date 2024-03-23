package net.obvj.agents.util;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.obvj.agents.AgentManager;

/**
 * Unit tests for the {@link ApplicationContextFacade}
 *
 * @author oswaldo.bapvic.jr
 */
class ApplicationContextFacadeTest
{
    @Test
    void testNoInstancesAllowed()
    {
        assertThat(ApplicationContextFacade.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void testRetrieveAgentManagerBean()
    {
        assertNotNull(ApplicationContextFacade.getBean(AgentManager.class));
    }

}
