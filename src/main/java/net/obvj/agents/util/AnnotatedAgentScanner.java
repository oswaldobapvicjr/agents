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

package net.obvj.agents.util;

import java.util.Set;
import java.util.stream.Collectors;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.AgentConfigurationException;

/**
 * Contains methods for scanning package(s) to find annotated agents.
 *
 * @author oswaldo.bapvic.jr
 */
public class AnnotatedAgentScanner
{

    private AnnotatedAgentScanner()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Scans the specified base package for agents.
     * <p>
     * <strong>NOTE: </strong> an empty {@code basePackage} string as a parameter may result
     * in a full class-path scan.
     *
     * @param basePackage the base package to search for annotated classes
     * @return a {@link Set} of {@link AgentConfiguration} objects from the objects found in
     *         the specified package, or an empty set; not null
     */
    public static Set<AgentConfiguration> scanPackage(String basePackage)
    {
        Set<String> classNames = findAnnotatedAgentClasses(basePackage);
        return classNames.stream()
                         .map(AnnotatedAgentScanner::toClass)
                         .map(AgentConfiguration::fromAnnotatedClass)
                         .collect(Collectors.toSet());
    }

    /**
     * Scans the specified base package for candidate agents.
     * <p>
     * <strong>NOTE: </strong> an empty {@code basePackage} string as a parameter may result
     * in a full class-path scan.
     *
     * @param basePackage the base package to search for annotated classes
     * @returns a list of candidate class names found in class path with the {@code @Agent}
     *          annotation, or an empty set; not null
     */
    protected static Set<String> findAnnotatedAgentClasses(String basePackage)
    {
        return AnnotationUtils.findClassesWithAnnotation(Agent.class, basePackage);
    }

    protected static Class<?> toClass(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException exception)
        {
            throw new AgentConfigurationException(exception);
        }
    }

}
