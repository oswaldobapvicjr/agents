package net.obvj.agents;

import net.obvj.agents.annotation.Agent;

/**
 * Available {@link Agent} types.
 *
 * @author oswaldo.bapvic.jr
 */
public enum AgentType
{
    /**
     * An object that runs a particular task periodically, given a configurable interval in
     * seconds, minutes, or hours.
     */
    TIMER("1 minute"),

    /**
     * An object that runs a particular task at specified times and dates, similar to the Cron
     * service available in Unix/Linux systems.
     */
    CRON("* * * * *");

    private final String defaultInterval;

    private AgentType(String defaultInterval)
    {
        this.defaultInterval = defaultInterval;
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

}
