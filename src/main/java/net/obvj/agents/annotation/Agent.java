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

    /**
     * Indicates whether or not the first execution should be delayed to cause the next ones
     * to occur on the interval boundary (default is {@code false}).
     * <p>
     * For example:
     * <ul>
     * <li><b>[modulate = true]:</b> if the current hour is 3h25 am and the agent interval is
     * {@code 4 hours} then the first execution will be delayed to 4h, and then the next ones
     * will occur at 8h, 12h, 16h, etc.</li>
     * <li><b>[modulate = false]:</b> if the current hour is 3h25 am and the agent interval is
     * {@code 4 hours} then the first execution will occur immediately (3h25), and then the
     * next ones will occur at 7h25, 11h25, 15h25, etc.</li>
     * </ul>
     * <p>
     * <b>NOTE:</b> This option is only applicable for agents of type {@link AgentType#TIMER}.
     *
     * @return a flag determining whether or not interval modulation is enabled for this agent
     * @since 0.2.0
     */
    boolean modulate() default false;

}
