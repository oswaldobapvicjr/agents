package net.obvj.agents.conf;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import net.obvj.agents.util.Functions;

@Component
public class GlobalConfigurationHolder
{
    private Map<Source, GlobalConfiguration> globalConfigurations;

    private Map<Source, Set<AgentConfiguration>> agentConfigurationsBySource = new EnumMap<>(Source.class);
    private Map<String, Set<AgentConfiguration>> agentConfigurationsByClassName = new HashMap<>();

    public GlobalConfigurationHolder()
    {
        globalConfigurations = loadGlobalConfigurationsBySource();
        fillAuxiliaryMaps();
    }

    private Map<Source, GlobalConfiguration> loadGlobalConfigurationsBySource()
    {
        return Arrays.stream(Source.values()).map(GlobalConfiguration::loadQuietly).filter(Optional::isPresent)
                .map(Optional::get).collect(toGlobalConfigurationBySourceMap());
    }

    private Collector<GlobalConfiguration, ?, EnumMap<Source, GlobalConfiguration>> toGlobalConfigurationBySourceMap()
    {
        return Collectors.toMap(GlobalConfiguration::getSource, Function.identity(), Functions.lastWinsMerger(),
                () -> new EnumMap<>(Source.class));
    }

    private void fillAuxiliaryMaps()
    {
        globalConfigurations.forEach((source, configuration) -> configuration.getAgents().stream()
                .map(builder -> builder.build(source)).forEach(this::add));
    }

    public void add(AgentConfiguration configuration)
    {
        Source source = configuration.getSource();
        agentConfigurationsBySource.computeIfAbsent(source, key -> new HashSet<>()).add(configuration);

        String className = configuration.getClassName();
        agentConfigurationsByClassName.computeIfAbsent(className, key -> new HashSet<>()).add(configuration);
    }

    public void addAll(Collection<AgentConfiguration> configurations)
    {
        configurations.forEach(this::add);
    }

    public GlobalConfiguration getGlobalConfiguration(Source source)
    {
        return globalConfigurations.get(source);
    }

    public Set<AgentConfiguration> getAllAgentConfigurationsByClassName(String className)
    {
        return agentConfigurationsByClassName.get(className);
    }

    public Optional<AgentConfiguration> getHighestPrecedenceConfigurationByClassName(String className)
    {
        Set<AgentConfiguration> agentConfigurations = getAllAgentConfigurationsByClassName(className);
        if (CollectionUtils.isEmpty(agentConfigurations))
        {
            return Optional.empty();
        }
        return Optional.of(AgentConfiguration.getHighestPrecedenceConfiguration(agentConfigurations));
    }

}
