package net.obvj.agents.util;

import java.util.Objects;

import net.obvj.agents.AbstractAgent;
import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.InvalidClassException;

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
     * @param configuration the {@link AgentConfiguration} to be processed, not null
     * @return an {@link AbstractAgent} from the given {@link AgentConfiguration}, not null
     *
     * @throws NullPointerException  if a null {@link AgentConfiguration} is received
     * @throws InvalidClassException if any exception regarding a reflective operation (e.g.:
     *                               required class or method not found) occurs
     */
    public static AbstractAgent create(AgentConfiguration configuration)
    {
        Objects.requireNonNull(configuration, "The AgentConfiguration must not be null");
        AgentType type = configuration.getType();
        return type.getFactoryFunction().apply(configuration);
    }

}
