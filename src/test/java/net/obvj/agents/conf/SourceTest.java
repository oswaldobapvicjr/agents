package net.obvj.agents.conf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import net.obvj.agents.conf.AgentConfiguration.Builder;

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
    }

}
