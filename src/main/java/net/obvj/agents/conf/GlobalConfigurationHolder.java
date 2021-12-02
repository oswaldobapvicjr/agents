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

package net.obvj.agents.conf;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import net.obvj.confectory.ConfigurationBuilder;
import net.obvj.confectory.TypeSafeConfigurationContainer;
import net.obvj.confectory.mapper.JacksonJsonToObjectMapper;
import net.obvj.confectory.mapper.JacksonXMLToObjectMapper;
import net.obvj.confectory.mapper.JacksonYAMLToObjectMapper;
import net.obvj.confectory.source.SourceFactory;

/**
 * An object that holds multiple configuration objects for the several configuration
 * sources.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.2.0
 */
@Component
public class GlobalConfigurationHolder
{
    private final TypeSafeConfigurationContainer<GlobalConfiguration> container;

    private Map<String, AgentConfiguration> agentsByClassName;

    /**
     * Builds a {@link GlobalConfigurationHolder}, loaded with configuration data mapped from
     * all of the supported configuration sources.
     */
    public GlobalConfigurationHolder()
    {
        this(defaultContainer());
    }

    protected GlobalConfigurationHolder(TypeSafeConfigurationContainer<GlobalConfiguration> container)
    {
        this.container = container;
        fillAuxiliaryMap();
    }

    private static TypeSafeConfigurationContainer<GlobalConfiguration> defaultContainer()
    {
        TypeSafeConfigurationContainer<GlobalConfiguration> container = new TypeSafeConfigurationContainer<>();

        container.add(new ConfigurationBuilder<GlobalConfiguration>().precedence(3)
                .source(SourceFactory.classpathFileSource("agents.xml"))
                .mapper(new JacksonXMLToObjectMapper<>(GlobalConfiguration.class)).optional().lazy().build());

        container.add(new ConfigurationBuilder<GlobalConfiguration>().precedence(4)
                .source(SourceFactory.classpathFileSource("agents.json"))
                .mapper(new JacksonJsonToObjectMapper<>(GlobalConfiguration.class)).optional().lazy().build());

        container.add(new ConfigurationBuilder<GlobalConfiguration>().precedence(5)
                .source(SourceFactory.classpathFileSource("agents.yaml"))
                .mapper(new JacksonYAMLToObjectMapper<>(GlobalConfiguration.class)).optional().lazy().build());

        return container;
    }

    private void fillAuxiliaryMap()
    {
        GlobalConfiguration config = container.getBean();
        agentsByClassName = getAgentConfigurationBuilders(config).stream()
                .map(AgentConfiguration.Builder::build)
                .collect(Collectors.toMap(AgentConfiguration::getClassName, Function.identity()));
    }

    private List<AgentConfiguration.Builder> getAgentConfigurationBuilders(GlobalConfiguration globalConfiguration)
    {
        return globalConfiguration != null ? globalConfiguration.getAgents() : Collections.emptyList();
    }

    /**
     * Returns the highest-precedence {@link AgentConfiguration} object associated with a
     * given class name.
     *
     * @param className the agent class name to be searched
     * @return the highest-precedence available {@link AgentConfiguration} object for the
     *         specified class name, or {@link Optional#empty()} if no associated
     *         {@link AgentConfiguration} found
     */
    public Optional<AgentConfiguration> getHighestPrecedenceConfigurationByAgentClassName(String className)
    {
        return Optional.ofNullable(agentsByClassName.get(className));
    }

}
