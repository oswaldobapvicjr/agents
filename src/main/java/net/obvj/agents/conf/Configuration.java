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

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.obvj.agents.conf.AgentConfiguration.Builder;

/**
 * An object containing configuration data from a particular source.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.2.0
 */
public class Configuration
{
    @JsonProperty("agents")
    private List<Builder> agents;

    @JsonProperty("global")
    private GlobalConfiguration globalConfiguration = new GlobalConfiguration();

    /**
     * Constructs a new {@link Configuration}.
     */
    public Configuration()
    {
        // Empty constructor to allow for file deserialization
    }

    /**
     * Returns a list of {@link AgentConfiguration.Builder} (candidates), as retrieved by the
     * configuration container.
     *
     * @return a list of {@link AgentConfiguration.Builder} objects
     * @see AgentConfiguration
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
     * @return the statisticsConfiguration
     */
    public GlobalConfiguration getGlobalConfiguration()
    {
        return globalConfiguration;
    }

    /**
     * @param globalConfiguration the globalConfiguration to set
     */
    public void setGlobalConfiguration(GlobalConfiguration globalConfiguration)
    {
        this.globalConfiguration = globalConfiguration;
    }

    /**
     * Returns a string representation of this {@link Configuration}.
     *
     * @return a string representation of this {@link Configuration}
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("agents", agents)
                .append("global", globalConfiguration)
                .build();
    }

}
