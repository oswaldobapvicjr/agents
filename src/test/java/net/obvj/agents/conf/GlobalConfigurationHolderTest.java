package net.obvj.agents.conf;

import static net.obvj.agents.AgentType.CRON;
import static net.obvj.agents.AgentType.TIMER;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @InjectMocks
    private GlobalConfigurationHolder globalConfigurationHolder;


}
