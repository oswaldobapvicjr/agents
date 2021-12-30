package net.obvj.agents.conf;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.containsAll;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration.Builder;

/**
 * Unit tests for the {@link Configuration}.
 *
 * @author oswaldo.bapvic.jr
 */
class ConfigurationTest
{
    private static final String NAME1 = "name1";
    private static final String NAME2 = "name2";
    private static final String CLASS_NAME1 = "className1";
    private static final String CLASS_NAME2 = "className2";
    private static final String INTERVAL1 = "interval1";
    private static final String INTERVAL2 = "interval2";

    private static final Builder BUILDER_AGENT_1 = new AgentConfiguration.Builder().type(AgentType.TIMER)
            .className(CLASS_NAME1).name(NAME1).interval(INTERVAL1).modulate(true).enableStatistics(true);

    private static final Builder BUILDER_AGENT_2 = new AgentConfiguration.Builder().type(AgentType.CRON)
            .className(CLASS_NAME2).name(NAME2).interval(INTERVAL2).modulate(false).enableStatistics(false);

    private Configuration configuration = new Configuration();

    @Test
    void toString_allFieldsSet_allFieldsPresent()
    {
        configuration.setAgents(Arrays.asList(BUILDER_AGENT_1, BUILDER_AGENT_2));

        String string = configuration.toString().replaceAll("\"", "");
        System.out.println(string);
        assertThat(string,
                containsAll("agents:[",
                        "{name:name1,className:className1,type:TIMER,interval:interval1,modulate:true,enableStatistics:true",
                        "{name:name2,className:className2,type:CRON,interval:interval2,modulate:false,enableStatistics:false"));
    }

}
