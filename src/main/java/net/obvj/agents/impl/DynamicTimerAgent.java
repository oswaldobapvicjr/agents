package net.obvj.agents.impl;

import net.obvj.agents.TimerAgent;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.InvalidClassException;

/**
 * A {@link TimerAgent} that runs a dynamic agent object.
 *
 * @author oswaldo.bapvic.jr
 */
public class DynamicTimerAgent extends TimerAgent
{
    private final DynamicAgent annotatedAgent;

    /**
     * Creates a new DynamicTimerAgent for the given {@link AgentConfiguration}.
     *
     * @param configuration the {@link AgentConfiguration} to be parsed
     * @throws InvalidClassException if any exception regarding a reflective operation (e.g.:
     *                               class or method not found) occurs
     */
    public DynamicTimerAgent(AgentConfiguration configuration)
    {
        super(configuration);
        annotatedAgent = new DynamicAgent(configuration);
    }

    /**
     * Executes the method annotated with {@code AgentTask} in the annotated agent instance.
     */
    @Override
    protected void runTask()
    {
        annotatedAgent.runAgentTask();
    }

    /**
     * @return the metadata associated with this AnnotatedTimerAgent
     */
    protected DynamicAgent getMetadata()
    {
        return annotatedAgent;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AnnotatedTimerAgent$").append(annotatedAgent.getAgentClass().getName());
        return builder.toString();
    }

}
