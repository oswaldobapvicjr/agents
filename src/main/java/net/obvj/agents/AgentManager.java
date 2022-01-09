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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.conf.ConfigurationHolder;
import net.obvj.agents.util.AgentFactory;
import net.obvj.agents.util.AnnotatedAgentScanner;
import net.obvj.agents.util.ApplicationContextFacade;
import net.obvj.agents.util.Exceptions;

/**
 * A component that provides methods for Agents maintenance.
 *
 * @author oswaldo.bapvic.jr
 */
@Component
public class AgentManager
{
    private static final String MSG_INVALID_AGENT = "Invalid agent: %s";
    private static final String MSG_AGENT_STARTED_PLEASE_STOP_FIRST = "'%s' is started. Please stop the agent before this operation.";

    private static final Logger LOG = LoggerFactory.getLogger(AgentManager.class);

    private Map<String, AbstractAgent> agentsByName = new TreeMap<>();
    private Map<String, AgentConfiguration> agentsByClass = new TreeMap<>();

    private ConfigurationHolder configurationHolder;

    protected AgentManager(@Autowired ConfigurationHolder holder)
    {
        configurationHolder = holder;
    }

    /**
     * Returns the default instance of this component.
     *
     * @return the default instance of this {@link AgentManager}
     */
    public static AgentManager defaultInstance()
    {
        return ApplicationContextFacade.getBean(AgentManager.class);
    }

    /**
     * Scans the specified base package for agent candidates, then looks for higher precedence
     * configuration sources and, finally, instantiates the agents, and keeps theirs reference
     * for management purposes.
     *
     * @param basePackage the base package to search for agent candidates
     */
    public void scanPackage(String basePackage)
    {
        Collection<AgentConfiguration> agentCandidates = AnnotatedAgentScanner.scanPackage(basePackage);

        if (agentCandidates.isEmpty())
        {
            LOG.warn("No agent found in base package \"{}\"", basePackage);
            return;
        }

        LOG.info("Instantiating agent(s)...");

        agentCandidates.stream()
                .map(this::findHighestPrecedenceConfiguration)
                .map(this::instantiateAgentQuietly)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(this::addAgent);

        LOG.info("Instantiation complete. Now managing {} agents: {}", agentsByClass.size(), agentsByClass.values());
    }

    /**
     * Looks up the global configuration for another higher-precedence configuration for the
     * agent class, returning the highest-precedence {@link AgentConfiguration} to be applied.
     *
     * @param agentConfiguration the source {@link AgentConfiguration}
     * @return the highest-precedence {@link AgentConfiguration} for the same agent class, or
     *         the self parameter object, if no higher-precedence configuration found
     * @since 0.2.0
     */
    private AgentConfiguration findHighestPrecedenceConfiguration(AgentConfiguration agentConfiguration)
    {
        return configurationHolder.getHighestPrecedenceConfigurationByAgentClassName(agentConfiguration.getClassName())
                .orElse(agentConfiguration);
    }

    /**
     * Creates an Agent for the given {@link AgentConfiguration}, provided that the same agent
     * class was not loaded before.
     *
     * @param agentConfiguration the {@link AgentConfiguration} to be parsed
     * @return an Optional which may contain a valid {@link Agent}, or
     *         {@link Optional#empty()} if unable to parse the given configuration, or if the
     *         agent was already loaded before
     */
    private Optional<AbstractAgent> instantiateAgentQuietly(AgentConfiguration agentConfiguration)
    {
        if (agentsByClass.containsKey(agentConfiguration.getClassName()))
        {
            LOG.debug("The agent {} was already instantiated", agentConfiguration.getClass());
            return Optional.empty();
        }
        return instantiateAgent(agentConfiguration);
    }

    /**
     * Creates an Agent for the given {@link AgentConfiguration}.
     *
     * @param agentConfiguration the {@link AgentConfiguration} to be parsed
     * @return an Optional which may contain a valid {@link Agent}, or
     *         {@link Optional#empty()} if unable to parse the given configuration
     */
    private Optional<AbstractAgent> instantiateAgent(AgentConfiguration agentConfiguration)
    {
        LOG.debug("Instantiating agent {}...", agentConfiguration.getClassName());
        try
        {
            return Optional.of(AgentFactory.create(agentConfiguration));
        }
        catch (Exception exception)
        {
            LOG.error("Error loading agent: {}", agentConfiguration.getClassName(), exception);
            return Optional.empty();
        }
    }

    /**
     * Adds a new agent for maintenance
     *
     * @param agent the agent to be registered
     */
    protected void addAgent(AbstractAgent agent)
    {
        AgentConfiguration configuration = agent.getConfiguration();
        String name = configuration.getName();
        String agentClass = configuration.getClassName();

        agentsByName.put(name, agent);
        agentsByClass.put(agentClass, configuration);
        LOG.debug("New agent added: {} (Object ID = {})", agentClass, ObjectUtils.getIdentityHexString(agent));
    }

    /**
     * Searches and returns the agent with the specified name in this manager's scope.
     *
     * @param name the agent name to search for, not null
     * @return the agent object associated with the specified name in this manager's scope
     *
     * @throws IllegalArgumentException if no agent with the given name found, or if the
     *                                  specified string is either null or empty
     */
    public AbstractAgent findAgentByName(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw Exceptions.illegalArgument("The name cannot be null or empty");
        }
        if (agentsByName.containsKey(name))
        {
            return agentsByName.get(name);
        }
        throw Exceptions.illegalArgument(MSG_INVALID_AGENT, name);
    }

    /**
     * Removes an agent identified by the given name, if it is not started
     *
     * @param name the identifier of the agent to be removed
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws IllegalStateException    if the requested agent is started
     */
    public void removeAgent(String name)
    {
        AbstractAgent agent = findAgentByName(name);
        if (agent.isStarted() || agent.isRunning())
        {
            throw Exceptions.illegalState(MSG_AGENT_STARTED_PLEASE_STOP_FIRST, name);
        }
        agentsByName.remove(name);
    }

    /**
     * Creates a new instance of the agent identified by the given name.
     *
     * @param name the identifier of the agent to be reset
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws IllegalStateException    if the agent is either started or running
     */
    public void resetAgent(String name)
    {
        AbstractAgent agent = findAgentByName(name);
        if (agent.isStarted() || agent.isRunning())
        {
            throw Exceptions.illegalState(MSG_AGENT_STARTED_PLEASE_STOP_FIRST, name);
        }

        LOG.info("Resetting agent: {}", name);

        String agentClass = agent.getConfiguration().getClassName();
        AgentConfiguration agentConfig = agentsByClass.get(agentClass);
        AbstractAgent newAgent = AgentFactory.create(agentConfig);

        addAgent(newAgent);
    }

    /**
     * Starts the agent identified by the given name.
     *
     * @param name the identifier of the agent to be started
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws IllegalStateException    if the requested agent is already started
     */
    public void startAgent(String name)
    {
        startAgent(findAgentByName(name));
    }

    /**
     * Posts immediate execution of the agent identified by the given name.
     *
     * @param name the identifier of the agent to be run
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public void runNow(String name)
    {
        findAgentByName(name).run(true);
    }

    /**
     * Posts a graceful stop request for the agent identified by the given name.
     *
     * @param name the identifier of the agent to be stopped
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public void stopAgent(String name)
    {
        findAgentByName(name).stop();
    }

    public Collection<AbstractAgent> getAgents()
    {
        return agentsByName.values();
    }

    /**
     * Returns a flag indicating whether an agent is running or not.
     *
     * @param name the identifier of the agent to be reset
     * @return {@code true} if the agent is running, otherwise {@code false}
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public boolean isAgentRunning(String name)
    {
        return findAgentByName(name).isRunning();
    }

    /**
     * Returns a flag indicating whether an agent is started or not.
     *
     * @param name the identifier of the agent to be reset
     * @return {@code true} if the agent is started, otherwise {@code false}
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public boolean isAgentStarted(String name)
    {
        return findAgentByName(name).isStarted();
    }

    /**
     * Returns a JSON string containing agent status information for reporting.
     *
     * @param name the identifier of the agent to be reported
     * @return a string in JSON format containing agent status information and other metadata
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public String getAgentStatusJson(String name)
    {
        return findAgentByName(name).getStatusJson();
    }

    public void startAllAgents()
    {
        LOG.info("Starting agents...");
        getAgents().forEach(this::startAgent);
        LOG.info("All agents started successfully...");
    }

    protected void startAgent(AbstractAgent agent)
    {
        agent.start();
    }

}
