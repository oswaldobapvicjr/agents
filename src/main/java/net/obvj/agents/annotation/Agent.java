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
     * If not specified, the name of this Agent will be the simple name of the class being
     * annotated.
     */
    String name() default "";

    /**
     * The agent type.
     * <p>
     * If not specified, {@link AgentType#TIMER} will be considered.
     */
    AgentType type() default AgentType.TIMER;

    /**
     * The frequency of the task.
     * <p>
     * If not specified, the default interval of 1 minute will be considered.
     */
    String frequency() default "";

}
