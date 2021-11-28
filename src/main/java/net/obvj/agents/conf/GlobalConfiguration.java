package net.obvj.agents.conf;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.obvj.agents.conf.AgentConfiguration.Builder;

/**
 * An object containing global configuration data from a particular {@link Source}.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.2.0
 */
public class GlobalConfiguration
{
    @JsonProperty("agents")
    private List<Builder> agents;

    /**
     * Constructs a new {@link GlobalConfiguration}.
     */
    public GlobalConfiguration()
    {
        // Empty constructor to allow for file deserialization
    }

    /**
     * Returns a list of {@link AgentConfiguration} candidates, as retrieved by the associated
     * configuration {@link Source}.
     *
     * @return a list of {@link AgentConfiguration} builders, as retrieved by the associated
     *         configuration {@link Source}
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
     * Returns a string representation of this {@link GlobalConfiguration}.
     *
     * @return a string representation of this {@link GlobalConfiguration}
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("agents", agents)
                .build();
    }

}
