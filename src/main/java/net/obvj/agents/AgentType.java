package net.obvj.agents;

import java.util.function.Function;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.impl.AbstractAgent;
import net.obvj.agents.impl.AnnotatedCronAgent;
import net.obvj.agents.impl.AnnotatedTimerAgent;

/**
 * Enumerates available {@link Agent} types and associated facilities.
 *
 * @author oswaldo.bapvic.jr
 */
public enum AgentType
{
    /**
     * An object that runs a particular task periodically, given a configurable interval in
     * seconds, minutes, or hours.
     */
    TIMER("1 minute", AnnotatedTimerAgent::new),

    /**
     * An object that runs a particular task at specified times and dates, similar to the Cron
     * service available in Unix/Linux systems.
     */
    CRON("* * * * *", AnnotatedCronAgent::new);

    private final String defaultInterval;
    private final Function<AgentConfiguration, AbstractAgent> factoryFunction;

    private AgentType(String defaultInterval, Function<AgentConfiguration, AbstractAgent> factoryFunction)
    {
        this.defaultInterval = defaultInterval;
        this.factoryFunction = factoryFunction;
    }

    /**
     * Returns the default interval for an agent type.
     *
     * @return the default interval
     */
    public String getDefaultInterval()
    {
        return defaultInterval;
    }

    /**
     * Returns the default factory {@link Function} to be applied for instantiating new agents
     * of this type.
     *
     * @return the factory {@link Function}
     */
    public Function<AgentConfiguration, AbstractAgent> getFactoryFunction()
    {
        return factoryFunction;
    }

}
