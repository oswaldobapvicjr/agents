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

import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.util.ReflectionUtils;

import net.obvj.agents.annotation.Run;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.InvalidClassException;
import net.obvj.agents.util.AnnotationUtils;
import net.obvj.agents.util.AnnotationUtils.MethodFilter;

/**
 * An object that prepares and holds the required metadata and infrastructure for the
 * execution of a dynamic agent, which may vary depending on the associated
 * {@link AgentConfiguration}.
 *
 * @author oswaldo.bapvic.jr
 */
public class DynamicAgent
{
    private final Class<?> agentClass;
    private final Method agentTaskMethod;
    private final Object agentInstance;

    /**
     * Validates annotations and prepares all objects for execution.
     *
     * @param configuration the {@link AgentConfiguration} to be parsed
     * @throws InvalidClassException if any exception regarding a reflective operation (e.g.:
     *                               class or method not found) occurs
     */
    public DynamicAgent(AgentConfiguration configuration)
    {
        try
        {
            String agentClassName = configuration.getClassName();
            agentClass = Class.forName(agentClassName);
            agentTaskMethod = AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class, agentClass,
                    MethodFilter.NO_PARAMETER);
            agentInstance = ConstructorUtils.invokeConstructor(agentClass);
        }
        catch (ReflectiveOperationException cause)
        {
            throw new InvalidClassException(cause);
        }
    }

    /**
     * Invokes the method annotated as {@code @Run} for the agent.
     */
    public void runAgentTask()
    {
        ReflectionUtils.invokeMethod(agentTaskMethod, agentInstance);
    }

    /**
     * @return the agentClass
     */
    public Class<?> getAgentClass()
    {
        return agentClass;
    }

    /**
     * @return the annotated agent's runnable method
     */
    public Method getRunMethod()
    {
        return agentTaskMethod;
    }

    /**
     * @return the annotated agent's instance
     */
    public Object getAgentInstance()
    {
        return agentInstance;
    }

}
