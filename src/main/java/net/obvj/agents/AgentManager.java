package net.obvj.agents;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.conf.AnnotatedAgentScanner;
import net.obvj.agents.util.AgentFactory;
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
    private static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");

    private static final Logger LOG = LoggerFactory.getLogger(AgentManager.class);

    private Map<String, AbstractAgent> agentsByName = new TreeMap<>();
    private Map<String, AgentConfiguration> agentsByClass = new TreeMap<>();

    public static AgentManager getInstance()
    {
        return ApplicationContextFacade.getBean(AgentManager.class);
    }

    /**
     * Loads agent candidates retrieved by the {@link AgentLoader}.
     */
    public void scanPackage(String basePackage)
    {
        Collection<AgentConfiguration> agentCandidates = AnnotatedAgentScanner.scanPackage(basePackage);

        LOG.info("Instantiating agent(s)...");

        agentCandidates.stream().map(this::createAgent).filter(Optional::isPresent).map(Optional::get)
                .forEach(this::addAgent);

        LOG.info("{}/{} agent(s) loaded successfully: {}",
                agentsByName.size(), agentCandidates.size(), agentsByName.values());
    }

    /**
     * Creates an Agent for the given {@link AgentConfiguration}.
     *
     * @param agentConfiguration the {@link AgentConfiguration} to be parsed
     * @return an Optional which may contain a valid {@link Agent}, or
     *         {@link Optional#empty()} if unable to parse the given configuration
     */
    private Optional<AbstractAgent> createAgent(AgentConfiguration agentConfiguration)
    {
        LOG.debug("Instantiating agent {}...", agentConfiguration.getClassName());

        try
        {
            return Optional.of(AgentFactory.create(agentConfiguration));
        }
        catch (Exception exception)
        {
            LOG.error("Error loading agent: {}", agentConfiguration.getName(), exception);
            return Optional.empty();
        }
    }

    /**
     * Registers a new agent for maintenance
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
     * Returns a string containing agent status information for reporting.
     *
     * @param name the identifier of the agent to be reported
     * @return a String containing agent status information and other metadata
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public String getAgentStatusStr(String name)
    {
        return getAgentStatusStr(name, true);
    }

    protected String getAgentStatusStr(String name, boolean prettyPrinting)
    {
        String statusString = findAgentByName(name).getStatusString();
        return prettyPrinting ? getPrettyPrintedJson(statusString) : statusString;
    }

    /**
     * @param string the JSON object representation to be converted
     * @return a JSON representation that fits in a page for pretty printing
     */
    private String getPrettyPrintedJson(String string)
    {
        JsonObject jsonObject = new Gson().fromJson(string, JsonObject.class);
        String prettyPrintedJson = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
        return prettyPrintedJson.replace("\n", SYSTEM_LINE_SEPARATOR);
    }

    public void startAllAgents()
    {
        LOG.info("Starting agents...");
        getAgents().forEach(this::startAgent);
    }

    protected void startAgent(AbstractAgent agent)
    {
        agent.start();
    }

}
