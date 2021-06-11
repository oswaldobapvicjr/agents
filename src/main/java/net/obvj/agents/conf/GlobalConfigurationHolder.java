package net.obvj.agents.conf;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.obvj.agents.util.Functions;

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
    private Map<Source, GlobalConfiguration> globalConfigurations;

    private Map<String, Set<AgentConfiguration>> agentConfigurationsByClassName = new HashMap<>();

    /**
     * Builds a {@link GlobalConfigurationHolder}, loaded with configuration data mapped from
     * all of the supported configuration sources.
     */
    protected GlobalConfigurationHolder()
    {
        globalConfigurations = loadGlobalConfigurationsBySource();
        fillAuxiliaryMap();
    }

    /**
     * Iterates through all supported configuration sources, then loads the mapped
     * configuration data into {@link GlobalConfiguration} objects.
     *
     * @return a map of {@link GlobalConfiguration} objects by {@link Source}
     */
    private Map<Source, GlobalConfiguration> loadGlobalConfigurationsBySource()
    {
        return Arrays.stream(Source.values())
                     .map(GlobalConfiguration::fromSource)
                     .filter(Optional::isPresent)
                     .map(Optional::get)
                     .collect(toGlobalConfigurationBySourceMap());
    }

    private Collector<GlobalConfiguration, ?, EnumMap<Source, GlobalConfiguration>> toGlobalConfigurationBySourceMap()
    {
        return Collectors.toMap(GlobalConfiguration::getSource, Function.identity(), Functions.lastWinsMerger(),
                () -> new EnumMap<>(Source.class));
    }

    private void fillAuxiliaryMap()
    {
        globalConfigurations.forEach((source, configuration) -> configuration.getAgents().stream()
                .map(builder -> builder.build(source))
                .forEach(this::add));
    }

    private void add(AgentConfiguration configuration)
    {
        String className = configuration.getClassName();
        getAgentConfigurationsMapByClassName().computeIfAbsent(className, key -> new HashSet<>())
                                              .add(configuration);
    }

    /**
     * Returns the highest-precedence {@link AgentConfiguration} object associated with a
     * given class name.
     *
     * @param className the agent class name to be searched
     * @return the highest-precedence {@link AgentConfiguration} for the specified class name,
     *         or {@link Optional#empty()} if no associated {@link AgentConfiguration} found
     */
    public Optional<AgentConfiguration> getHighestPrecedenceAgentConfigurationByClassName(String className)
    {
        Set<AgentConfiguration> agentConfigurations = agentConfigurationsByClassName.get(className);
        if (CollectionUtils.isEmpty(agentConfigurations))
        {
            return Optional.empty();
        }
        return Optional.of(AgentConfiguration.getHighestPrecedenceConfiguration(agentConfigurations));
    }

    protected Map<String, Set<AgentConfiguration>> getAgentConfigurationsMapByClassName()
    {
        return agentConfigurationsByClassName;
    }

}
