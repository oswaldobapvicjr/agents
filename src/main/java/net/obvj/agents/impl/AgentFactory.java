package net.obvj.agents.impl;

import java.util.Objects;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration;

/**
 * A factory that creates {@link AbstractAgent} objects based on given
 * {@link AgentConfiguration}.
 *
 * @author oswaldo.bapvic.jr
 */
public class AgentFactory
{

    private AgentFactory()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Creates a new agent instance from the given {@link AgentConfiguration}.
     *
     * @throws NullPointerException if a null agent configuration is received
     */
    public static AbstractAgent create(AgentConfiguration configuration)
    {
        Objects.requireNonNull(configuration, "The AgentConfiguration must not be null");
        AgentType type = configuration.getType();
        return type.getFactoryFunction().apply(configuration);
    }

}
