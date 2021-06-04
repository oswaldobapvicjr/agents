package net.obvj.agents.conf;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.obvj.agents.conf.AgentConfiguration.Builder;

public class GlobalConfiguration
{
    public static final GlobalConfiguration EMPTY = new GlobalConfiguration(Collections.emptyList(), null);

    @JsonProperty("agents")
    private List<Builder> agents;

    private Source source;

    public GlobalConfiguration()
    {
        // Empty constructor to allow for file deserialization
    }

    private GlobalConfiguration(List<Builder> agents, Source source)
    {
        this.agents = agents;
        this.source = source;
    }

    /**
     * @return the agents
     */
    public List<Builder> getAgents()
    {
        return agents;
    }

    /**
     * @param agents the agents to set
     */
    protected void setAgents(List<Builder> agents)
    {
        this.agents = agents;
    }

    /**
     * @return the source
     */
    public Source getSource()
    {
        return source;
    }

    /**
     * @param source the source to set
     */
    protected void setSource(Source source)
    {
        this.source = source;
    }

    public static Optional<GlobalConfiguration> loadQuietly(Source source)
    {
        return source != null ? source.loadGlobalConfigurationQuietly() : Optional.empty();
    }

}
