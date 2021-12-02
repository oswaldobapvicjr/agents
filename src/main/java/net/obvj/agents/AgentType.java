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

package net.obvj.agents;

import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.impl.DynamicCronAgent;
import net.obvj.agents.impl.DynamicTimerAgent;

/**
 * Enumerates available {@link Agent} types and associated facilities.
 *
 * @author oswaldo.bapvic.jr
 */
public enum AgentType
{
    /**
     * An object that runs a particular task periodically, given a configurable interval in
     * seconds, minutes, or hours.
     */
    @JsonProperty("timer")
    TIMER("1 minute", DynamicTimerAgent::new),

    /**
     * An object that runs a particular task at specified times and dates, similar to the Cron
     * service available in Unix/Linux systems.
     */
    @JsonProperty("cron")
    CRON("* * * * *", DynamicCronAgent::new);

    private final String defaultInterval;
    private final Function<AgentConfiguration, AbstractAgent> factoryFunction;

    private AgentType(String defaultInterval, Function<AgentConfiguration, AbstractAgent> factoryFunction)
    {
        this.defaultInterval = defaultInterval;
        this.factoryFunction = factoryFunction;
    }

    /**
     * Returns the default interval for an agent type.
     *
     * @return the default interval
     */
    public String getDefaultInterval()
    {
        return defaultInterval;
    }

    /**
     * Returns the default factory {@link Function} to be applied for instantiating new agents
     * of this type.
     *
     * @return the factory {@link Function}
     */
    public Function<AgentConfiguration, AbstractAgent> getFactoryFunction()
    {
        return factoryFunction;
    }

}
