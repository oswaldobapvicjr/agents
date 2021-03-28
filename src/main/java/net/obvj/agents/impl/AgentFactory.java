package net.obvj.agents.impl;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.util.Exceptions;

/**
 * A factory that creates {@link AbstractAgent} objects based on given {@link AgentConfiguration}.
 *
 * @author oswaldo.bapvic.jr
 */
public class AgentFactory
{
    private static final Map<AgentType, Function<AgentConfiguration, AbstractAgent>> IMPLEMENTATIONS = new EnumMap<>(AgentType.class);

    static
    {
        IMPLEMENTATIONS.put(AgentType.TIMER, AnnotatedTimerAgent::new);
        IMPLEMENTATIONS.put(AgentType.CRON, AnnotatedCronAgent::new);
    }

    private AgentFactory()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Creates a new Agent from the given {@link AgentConfiguration}.
     *
     * @throws NullPointerException     if a null agent configuration is received
     * @throws IllegalArgumentException if an unknown agent type is received
     */
    public static AbstractAgent create(AgentConfiguration configuration)
    {
        AgentType type = configuration.getType();

        if (IMPLEMENTATIONS.containsKey(type))
        {
            return IMPLEMENTATIONS.get(type).apply(configuration);
        }
        throw Exceptions.illegalArgument("Unknown agent type: \"%s\"", type);
    }

}
