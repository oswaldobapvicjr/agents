package net.obvj.agents.conf;

import java.util.List;
import java.util.Optional;

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

    private Source source;

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
     * @return a list of {@link AgentConfiguration} builders, as retrieved be the associated
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
     * Returns the external source associated with this {@link GlobalConfiguration}.
     *
     * @return the {@link Source} associated with this {@link GlobalConfiguration}.
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

    /**
     * Creates a {@link GlobalConfiguration} object from the specified {@link Source}.
     * <p>
     * <strong>NOTE:</strong> this method acts quietly, i.e., in case of exceptions, an empty
     * object is always returned.
     *
     * @param source the {@link Source} which configuration is to be loaded
     * @return a {@link GlobalConfiguration} object, loaded by the specified {@link Source},
     *         or {@link Optional#empty()} if the specified source is null, or unable to parse
     *         the configuration source; never {@code null}
     */
    public static Optional<GlobalConfiguration> from(Source source)
    {
        return source != null ? source.loadGlobalConfigurationQuietly() : Optional.empty();
    }

}
