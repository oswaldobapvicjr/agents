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
