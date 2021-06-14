package net.obvj.agents.conf;

import static net.obvj.agents.AgentType.CRON;
import static net.obvj.agents.AgentType.TIMER;
import static net.obvj.agents.conf.Source.JSON;
import static net.obvj.agents.conf.Source.XML;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import net.obvj.agents.conf.AgentConfiguration.Builder;
import net.obvj.agents.test.agents.valid.DummyAgent;
import net.obvj.agents.test.agents.valid.TestTimerAgent1;

/**
 * Unit tests for the {@link GlobalConfigurationHolder} class.
 *
 * @author oswaldo.bapvic.jr
 */
@ExtendWith(MockitoExtension.class)
class GlobalConfigurationHolderTest
{
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String TIMER_AGENT = "TestTimerAgent1";

    private static final String INTERVAL_TIMER_8_HOURS = "8 hours";
    private static final String INTERVAL_TIMER_8760_HOURS = "8760 hours"; // once a year
    private static final String INTERVAL_CRON_ONCE_A_YEAR = "0 0 1 1 *";

    private static final String DUMMY_AGENT_CLASS_NAME = DummyAgent.class.getCanonicalName();
    private static final String TIMER_AGENT_CLASS_NAME = TestTimerAgent1.class.getCanonicalName();

    private static final Builder DUMMY_AGENT_XML = new Builder()
            .type(TIMER)
            .name(DUMMY_AGENT)
            .className(DUMMY_AGENT_CLASS_NAME)
            .interval(INTERVAL_TIMER_8760_HOURS);

    private static final Builder TIMER_AGENT_XML = new Builder()
            .type(TIMER)
            .name(TIMER_AGENT)
            .className(TIMER_AGENT_CLASS_NAME)
            .interval(INTERVAL_TIMER_8_HOURS);

    private static final Builder DUMMY_AGENT_JSON = new Builder()
            .type(CRON)
            .name(DUMMY_AGENT)
            .className(DUMMY_AGENT_CLASS_NAME)
            .interval(INTERVAL_CRON_ONCE_A_YEAR);

    private static final List<Builder> AGENTS_XML = Arrays.asList(DUMMY_AGENT_XML, TIMER_AGENT_XML);
    private static final List<Builder> AGENTS_JSON = Collections.singletonList(DUMMY_AGENT_JSON);

    @Mock
    private GlobalConfiguration globalConfigXml;
    @Mock
    private GlobalConfiguration globalConfigJson;

    @Spy
    private Map<Source, GlobalConfiguration> globalConfigurations = new EnumMap<>(Source.class);

    @InjectMocks
    private GlobalConfigurationHolder globalConfigurationHolder;

    @BeforeEach
    public void setup()
    {
        globalConfigurations.put(XML, globalConfigXml);
        globalConfigurations.put(JSON, globalConfigJson);

        when(globalConfigXml.getAgents()).thenReturn(AGENTS_XML);
        when(globalConfigJson.getAgents()).thenReturn(AGENTS_JSON);

        globalConfigurationHolder.fillAuxiliaryMap();
    }

    @Test
    void getHighestPrecedenceAgentConfigurationByClassName_validClassFromXmlOnly_xmlConfigReturned()
    {
        AgentConfiguration agentConfiguration = globalConfigurationHolder
                .getHighestPrecedenceAgentConfigurationByClassName(TIMER_AGENT_CLASS_NAME).get();

        assertThat(agentConfiguration.getSource(), equalTo(XML));
        assertThat(agentConfiguration.getType(), equalTo(TIMER));
        assertThat(agentConfiguration.getInterval(), equalTo(INTERVAL_TIMER_8_HOURS));
    }

    @Test
    void getHighestPrecedenceAgentConfigurationByClassName_validClassFromXmlAndJson_jsonConfigReturned()
    {
        AgentConfiguration agentConfiguration = globalConfigurationHolder
                .getHighestPrecedenceAgentConfigurationByClassName(DUMMY_AGENT_CLASS_NAME).get();

        assertThat(agentConfiguration.getSource(), equalTo(JSON));
        assertThat(agentConfiguration.getType(), equalTo(CRON));
        assertThat(agentConfiguration.getInterval(), equalTo(INTERVAL_CRON_ONCE_A_YEAR));
    }

    @Test
    void getHighestPrecedenceAgentConfigurationByClassName_unknownClass_empty()
    {
        assertThat(globalConfigurationHolder.getHighestPrecedenceAgentConfigurationByClassName("Invalid"),
                equalTo(Optional.empty()));
    }

}
