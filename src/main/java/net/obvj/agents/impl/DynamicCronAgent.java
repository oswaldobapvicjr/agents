package net.obvj.agents.impl;

import net.obvj.agents.CronAgent;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.InvalidClassException;

/**
 * A {@link CronAgent} that runs a dynamic agent object.
 *
 * @author oswaldo.bapvic.jr
 */
public class DynamicCronAgent extends CronAgent
{
    private final DynamicAgent annotatedAgent;

    /**
     * Creates a new DynamicCronAgent for the given {@link AgentConfiguration}.
     *
     * @param configuration the {@link AgentConfiguration} to be parsed
     * @throws InvalidClassException if any exception regarding a reflective operation (e.g.:
     *                               class or method not found) occurs
     */
    public DynamicCronAgent(AgentConfiguration configuration)
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
     * @return the metadata
     */
    protected DynamicAgent getMetadata()
    {
        return annotatedAgent;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AnnotatedCronAgent$").append(annotatedAgent.getAgentClass().getName());
        return builder.toString();
    }

}
