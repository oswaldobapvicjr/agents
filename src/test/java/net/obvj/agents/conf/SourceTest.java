package net.obvj.agents.conf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.obvj.agents.AgentType;
import net.obvj.agents.conf.AgentConfiguration.Builder;
import net.obvj.agents.test.agents.valid.DummyAgent;

/**
 * Unit tests for the {@link Source} class.
 *
 * @author oswaldo.bapvic.jr
 */
class SourceTest
{

    @Test
    void getHighestPrecedenceSource_twoSources_highestPrecedenceSource()
    {
        assertThat(Source.getHighestPrecedenceSource(Source.XML, Source.ANNOTATION), is(Source.XML));
        assertThat(Source.getHighestPrecedenceSource(Source.JSON, Source.XML), is(Source.JSON));
        assertThat(Source.getHighestPrecedenceSource(Source.YAML, Source.JSON), is(Source.YAML));
    }

    @Test
    void getHighestPrecedenceSource_sinlgeSource_sameSource()
    {
        assertThat(Source.getHighestPrecedenceSource(Source.ANNOTATION), is(Source.ANNOTATION));
    }

    @Test
    void getHighestPrecedenceSource_noSource_default()
    {
        assertThat(Source.getHighestPrecedenceSource(), is(Source.DEFAULT));
    }

    @Test
    void loadGlobalConfigurationFileQuietly_fileNotFound_empty()
    {
        assertThat(Source.XML.loadGlobalConfigurationFileQuietly("notfound.xml"), is(Optional.empty()));
        assertThat(Source.JSON.loadGlobalConfigurationFileQuietly("notfound.json"), is(Optional.empty()));
        assertThat(Source.YAML.loadGlobalConfigurationFileQuietly("notfound.yaml"), is(Optional.empty()));
    }

    @Test
    void loadGlobalConfigurationFileQuietly_invalidFile_empty()
    {
        assertThat(Source.JSON.loadGlobalConfigurationFileQuietly("invalid/agents_bad_array.json"),
                is(Optional.empty()));
    }

    @Test
    void loadGlobalConfigurationFileQuietly_validJsonFile_success()
    {
        Optional<GlobalConfiguration> optional = Source.JSON.loadGlobalConfigurationFileQuietly("agents_alt1.json");
        assertThat(optional.isPresent(), is(true));
        GlobalConfiguration configuration = optional.get();
        assertThat(configuration.getSource(), is(Source.JSON));
        List<Builder> agents = configuration.getAgents();
        assertThat(agents.size(), is(1));
        assertAgent(agents.get(0), DummyAgent.class.getName(), null, AgentType.CRON, "*/5 * * * MON-FRI");
    }

    @Test
    void loadGlobalConfigurationFileQuietly_validYamlFile_success()
    {
        Optional<GlobalConfiguration> optional = Source.YAML.loadGlobalConfigurationFileQuietly("agents_alt1.yaml");
        assertThat(optional.isPresent(), is(true));
        GlobalConfiguration configuration = optional.get();
        assertThat(configuration.getSource(), is(Source.YAML));
        List<Builder> agents = configuration.getAgents();
        assertThat(agents.size(), is(2));
        assertAgent(agents.get(0), "com.mycompany.agents.Agent1", null, AgentType.TIMER, "30s");
        assertAgent(agents.get(1), "com.mycompany.agents.Agent2", null, AgentType.CRON, "0 0 * * SUN");
    }

    private void assertAgent(Builder agent, String className, String name, AgentType type, String interval)
    {
        assertThat(agent.getClassName(), is(className));
        assertThat(agent.getName(), is(name));
        assertThat(agent.getType(), is(type));
        assertThat(agent.getInterval(), is(interval));
    }

}
