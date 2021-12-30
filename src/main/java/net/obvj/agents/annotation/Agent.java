/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * Indicates whether or not the system will process and store statistical data (e.g.:
     * average execution duration) for this agent.
     *
     * @return a flag determining whether or not statistics are enabled for this agent
     * @since 0.3.0
     */
    boolean enableStatistics() default false;

}
