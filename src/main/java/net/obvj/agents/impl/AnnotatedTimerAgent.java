package net.obvj.agents.impl;

import net.obvj.agents.conf.AgentConfiguration;

/**
 * A {@link TimerAgent} that runs an object annotated with {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 */
public class AnnotatedTimerAgent extends TimerAgent
{
    private final AnnotatedAgent annotatedAgent;

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
     * @return the metadata
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
