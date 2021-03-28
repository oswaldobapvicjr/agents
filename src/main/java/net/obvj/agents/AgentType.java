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
    TIMER("timer"),

    /**
     * An object that runs a particular task at specified times and dates, similar to the Cron
     * service available in Unix/Linux systems.
     */
    CRON("cron");

    private final String value;

    private AgentType(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

}
