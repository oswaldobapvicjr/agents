package net.obvj.agents.impl;

import net.obvj.agents.CronAgent;
import net.obvj.agents.conf.AgentConfiguration;

/**
 * A {@link CronAgent} that runs an object annotated with {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 */
public class AnnotatedCronAgent extends CronAgent
{
    private final AnnotatedAgent annotatedAgent;

    public AnnotatedCronAgent(AgentConfiguration configuration)
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
        builder.append("AnnotatedCronAgent$").append(annotatedAgent.getAgentClass().getName());
        return builder.toString();
    }

}
