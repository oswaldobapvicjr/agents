package net.obvj.agents.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.obvj.agents.AgentType;

/**
 * Identifies the annotated class as an Agent.
 *
 * @author oswaldo.bapvic.jr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Agent
{
    /**
     * The agent name.
     * <p>
     * If not specified, the agent name will be equal to the canonical class name.
     *
     * @return the agent name
     */
    String name() default "";

    /**
     * The agent type.
     * <p>
     * If not specified, {@link AgentType#TIMER} will be considered.
     *
     * @return the agent type
     */
    AgentType type() default AgentType.TIMER;

    /**
     * The interval between executions.
     * <p>
     * If not specified, a default interval will be considered for, which is defined by the
     * agent type.
     *
     * @return a string representing the configured interval between executions
     */
    String interval() default "";

}
