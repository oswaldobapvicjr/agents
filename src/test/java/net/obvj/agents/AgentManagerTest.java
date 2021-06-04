package net.obvj.agents;

import static net.obvj.agents.AgentType.TIMER;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Sets;

import net.obvj.agents.AbstractAgent.State;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.conf.GlobalConfigurationHolder;
import net.obvj.agents.test.agents.invalid.TestAgentWithAllCustomParamsAndConstructorThrowsException;
import net.obvj.agents.test.agents.valid.DummyAgent;
import net.obvj.agents.util.AnnotatedAgentScanner;
import net.obvj.agents.util.ApplicationContextFacade;

/**
 * Unit tests for the {@link AgentManager} class.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class AgentManagerTest
{
    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS_NAME = DummyAgent.class.getCanonicalName();
    private static final String INVALID_AGENT_CLASS_NAME = TestAgentWithAllCustomParamsAndConstructorThrowsException.class.getCanonicalName();
    private static final String PACKAGE1 = "package1";
    private static final String UNKNOWN = "Unknown";

    private static final List<String> names = Arrays.asList(DUMMY_AGENT);

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder().type(TIMER)
            .name(DUMMY_AGENT).className(DUMMY_AGENT_CLASS_NAME).interval("8760 hour").build(); // once a year

    private static final AgentConfiguration INVALID_AGENT_CONFIG = new AgentConfiguration.Builder().type(TIMER)
            .className(INVALID_AGENT_CLASS_NAME).build();

    @Mock(lenient = true)
    private AbstractAgent dummyAgent;

    @Mock
    private GlobalConfigurationHolder globalConfigurationHolder;

    @InjectMocks
    private AgentManager manager = new AgentManager();

    private void mockDummyAgent()
    {
        when(dummyAgent.getConfiguration()).thenReturn(DUMMY_AGENT_CONFIG);
        when(dummyAgent.getName()).thenReturn(DUMMY_AGENT_CONFIG.getName());
        when(dummyAgent.getType()).thenReturn(TIMER);
    }

    private void prepareAgentManager(AbstractAgent... agents)
    {
        Arrays.stream(agents).forEach(manager::addAgent);
    }

    @Test
    void defaultInstance_validInstance()
    {
        try (MockedStatic<ApplicationContextFacade> applicationContextFacade = mockStatic(
                ApplicationContextFacade.class))
        {
            applicationContextFacade.when(() -> ApplicationContextFacade.getBean(AgentManager.class))
                    .thenReturn(manager);
            assertThat(AgentManager.defaultInstance(), equalTo(manager));
        }
    }

    @Test
    void scanPackage_mockedPackageAndValidAgentsOnly_instantiatesAllMockedAgents()
    {
        try (MockedStatic<AnnotatedAgentScanner> scanner = mockStatic(AnnotatedAgentScanner.class))
        {
            Set<AgentConfiguration> set = Sets.newHashSet(DUMMY_AGENT_CONFIG);
            scanner.when(() -> AnnotatedAgentScanner.scanPackage(PACKAGE1)).thenReturn(set);
            when(globalConfigurationHolder.getHighestPrecedenceAgentConfigurations()).thenReturn(set);
            manager.scanPackage(PACKAGE1);
        }
        assertEquals(1, manager.getAgents().size());
        assertNotNull(manager.findAgentByName(DUMMY_AGENT));
    }

    @Test
    void scanPackage_mockedPackageAndValidAndInvalidAgent_instantiatesValidAgentOnly()
    {

        try (MockedStatic<AnnotatedAgentScanner> scanner = mockStatic(AnnotatedAgentScanner.class))
        {
            Set<AgentConfiguration> set = Sets.newHashSet(DUMMY_AGENT_CONFIG, INVALID_AGENT_CONFIG);
            scanner.when(() -> AnnotatedAgentScanner.scanPackage(PACKAGE1)).thenReturn(set);
            when(globalConfigurationHolder.getHighestPrecedenceAgentConfigurations()).thenReturn(set);
            manager.scanPackage(PACKAGE1);
        }
        assertEquals(1, manager.getAgents().size());
        assertNotNull(manager.findAgentByName(DUMMY_AGENT));
    }

    @Test
    void getAgents_singleAgentRegistered_singletonList()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        Collection<AbstractAgent> agents = manager.getAgents();
        assertEquals(1, agents.size());
        List<String> managerNames = agents.stream().map(AbstractAgent::getName).collect(Collectors.toList());
        assertTrue(managerNames.containsAll(names));
    }

    @Test
    void findAgentByName_knownAgent_validAgent()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        assertEquals(dummyAgent, manager.findAgentByName(DUMMY_AGENT));
    }

    @Test
    void findAgentByName_unknown_illegalArgument()
    {
        assertThat(() -> manager.findAgentByName(UNKNOWN), throwsException(IllegalArgumentException.class));
    }

    @Test
    void findAgentByName_empty_illegalArgument()
    {
        assertThat(() -> manager.findAgentByName(""), throwsException(IllegalArgumentException.class));
    }

    @Test
    void start_knownAgent_callsStart()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        manager.startAgent(DUMMY_AGENT);
        verify(dummyAgent).start();
    }

    @Test
    void removeAgent_knownAgentAndNotRunning_removed()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isRunning()).thenReturn(false);
        manager.removeAgent(DUMMY_AGENT);
        assertEquals(0, manager.getAgents().size());
        assertFalse(manager.getAgents().contains(dummyAgent));
    }

    @Test
    void removeAgent_knownAgentAndRunning_illegalState()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isRunning()).thenReturn(true);
        assertThat(() -> manager.removeAgent(UNKNOWN), throwsException(IllegalArgumentException.class));
    }

    @Test
    void removeAgent_knownAgentAndStarted_illegalState()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isStarted()).thenReturn(true);
        assertThat(() -> manager.removeAgent(DUMMY_AGENT), throwsException(IllegalStateException.class));
    }

    @Test
    void removeAgent_unknown_illegalArgument()
    {
        assertThat(() -> manager.removeAgent(UNKNOWN), throwsException(IllegalArgumentException.class));
    }

    @Test
    void getAgentStatusStr_knownAgent_expectedString()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.getStatusString()).thenReturn("{\"statusStr\":\"statusStr1\"}");
        assertThat(manager.getAgentStatusStr(DUMMY_AGENT), containsAll("\"statusStr\"", "\"statusStr1\""));
    }

    @Test
    void getAgentStatusStr_knownAgentAndPrettyPrintingFalse_expectedString()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        String expectedValue = "statusStr1";
        when(dummyAgent.getStatusString()).thenReturn(expectedValue);
        assertEquals(expectedValue, manager.getAgentStatusStr(DUMMY_AGENT, false));
    }

    @Test
    void stop_validAgent_callsStop()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isStopped()).thenReturn(false);
        manager.stopAgent(DUMMY_AGENT);
        verify(dummyAgent).stop();
    }

    @Test
    void run_validAgent_callsRun()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        manager.runNow(DUMMY_AGENT);
        verify(dummyAgent).run(true);
    }

    @Test
    void testResetAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        manager.resetAgent(DUMMY_AGENT);
        AbstractAgent newAgent = manager.findAgentByName(DUMMY_AGENT);

        // A new Agent instance is available
        assertNotSame(dummyAgent, newAgent);

        assertEquals(dummyAgent.getName(), newAgent.getName());
        assertEquals(dummyAgent.getType(), newAgent.getType());
        assertEquals(dummyAgent.getConfiguration(), newAgent.getConfiguration());
        assertEquals(State.SET, newAgent.getState());
    }

    @Test
    void reset_knownAgentAndStated_illegalState() throws ReflectiveOperationException
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isStarted()).thenReturn(true);
        assertThat(() -> manager.resetAgent(DUMMY_AGENT), throwsException(IllegalStateException.class));
    }

    @Test
    void reset_knownAgentAndRunning_illegalState() throws ReflectiveOperationException
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isRunning()).thenReturn(true);
        assertThat(() -> manager.resetAgent(DUMMY_AGENT), throwsException(IllegalStateException.class));
    }

    @Test
    void isAgentRunning_knownAgentAndRunning_true()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isRunning()).thenReturn(true);
        assertTrue(manager.isAgentRunning(DUMMY_AGENT));
    }

    @Test
    void isAgentRunning_knownAgentAndNotRunning_false()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isRunning()).thenReturn(false);
        assertFalse(manager.isAgentRunning(DUMMY_AGENT));
    }

    @Test
    void isAgentStarted_knownAgentAndStarted_true()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isStarted()).thenReturn(true);
        assertTrue(manager.isAgentStarted(DUMMY_AGENT));
    }

    @Test
    void isAgentStarted_knownAgentAndNotStarted_false()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        when(dummyAgent.isStarted()).thenReturn(false);
        assertFalse(manager.isAgentStarted(DUMMY_AGENT));
    }

    @Test
    void startAllAgents_managedAgentStarted()
    {
        mockDummyAgent();
        prepareAgentManager(dummyAgent);
        manager.startAllAgents();
        verify(dummyAgent).start();
    }

}
