package net.obvj.agents.impl;

import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.AgentConfigurationException;

/**
 * A {@link TimerAgent} that runs an object annotated with {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 */
public class AnnotatedTimerAgent extends TimerAgent
{
    private final AnnotatedAgent annotatedAgent;

    /**
     * Creates a new AnnotatedTimerAgent for the given {@link AgentConfiguration}.
     *
     * @param configuration the {@link AgentConfiguration} to be parsed
     * @throws AgentConfigurationException if any exception regarding a reflective operation
     *                                     (e.g.: class or method not found) occurs
     */
    public AnnotatedTimerAgent(AgentConfiguration configuration)
    {
        super(configuration);
        annotatedAgent = new AnnotatedAgent(configuration);
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
    protected AnnotatedAgent getMetadata()
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
