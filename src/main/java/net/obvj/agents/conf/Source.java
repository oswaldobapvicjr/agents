package net.obvj.agents.conf;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.util.Exceptions;

/**
 * Enumerates supported configuration sources and their precedence levels.
 *
 * @author oswaldo.bapvic.jr
 */
public enum Source
{

    /**
     * The source applicable for configuration performed via the {@link Agent} annotation.
     */
    ANNOTATION(1, null, null)
    {
        @Override
        public Optional<GlobalConfiguration> loadGlobalConfiguration()
        {
            return Optional.empty();
        }
    },

    /**
     * The source applicable for configuration performed programmatically with no specific
     * source defined.
     */
    DEFAULT(2, null, null)
    {
        @Override
        public Optional<GlobalConfiguration> loadGlobalConfiguration()
        {
            return Optional.empty();
        }
    },

    /**
     * The source applicable for configuration via XML file.
     */
    XML(3, "agents.xml", XmlMapper::new),

    /**
     * The source applicable for configuration via JSON file.
     */
    JSON(4, "agents.json", JsonMapper::new);

    private static final Logger LOGGER = LoggerFactory.getLogger(Source.class);

    private final int precedence;
    private final String resourceName;
    private final Supplier<ObjectMapper> objectMapperSupplier;

    private Source(int precedence, String resourceName, Supplier<ObjectMapper> objectMapperSupplier)
    {
        this.precedence = precedence;
        this.resourceName = resourceName;
        this.objectMapperSupplier = objectMapperSupplier;
    }

    public Optional<GlobalConfiguration> loadGlobalConfiguration()
    {
        LOGGER.debug("Looking for global {} configuration file", this);
        URL url = Source.class.getClassLoader().getResource(resourceName);
        if (url == null)
        {
            LOGGER.debug("Global {} configuration file \"{}\" not found", this, resourceName);
            return Optional.empty();
        }
        LOGGER.debug("Global {} configuration file \"{}\" found", this, resourceName);
        return loadGlobalConfiguration(url);
    }

    public Optional<GlobalConfiguration> loadGlobalConfiguration(URL url)
    {
        LOGGER.info("Reading global {} configuration file \"{}\"", this, url.getPath());
        try
        {
            ObjectMapper mapper = objectMapperSupplier.get();
            GlobalConfiguration configuration = mapper.readValue(url, GlobalConfiguration.class);
            configuration.setSource(this);
            LOGGER.info("{} configuration loaded successfully", this);
            return Optional.of(configuration);
        }
        catch (IOException exception)
        {
            throw Exceptions.agentConfiguration(exception, "Unable to parse configuration file: %s", url.toString());
        }
    }

    public Optional<GlobalConfiguration> loadGlobalConfigurationQuietly()
    {
        try
        {
            return loadGlobalConfiguration();
        }
        catch (AgentConfigurationException exception)
        {
            LOGGER.warn(exception.getMessage(), exception);
            return Optional.empty();
        }
    }

    /**
     * Returns the source with the highest precedence level among the specified sources
     *
     * @param sources an array of sources to be evaluated
     * @return the source with the highest precedence level among the specified sources, or
     *         {@link Source#DEFAULT} if no parameter specified
     */
    public static Source getHighestPrecedenceSource(Source... sources)
    {
        return getHighestPrecedenceSource(Arrays.stream(sources));
    }

    protected static Source getHighestPrecedenceSource(Stream<Source> stream)
    {
        return stream.filter(Objects::nonNull).sorted(Comparator.comparingInt(Source::getPrecedence).reversed())
                .findFirst().orElse(DEFAULT);
    }

    /**
     * Returns the precedence level for this source, ordered from highest to lowest, so that
     * higher-level sources have more precedence/importance than the other ones.
     *
     * @return the precedence level for this source
     */
    public int getPrecedence()
    {
        return precedence;
    }

}
