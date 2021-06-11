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
 * Enumerates the supported configuration sources and related metadata.
 * <p>
 * Each source has a fixed order of importance, which means that some of them may take
 * precedence over other ones. The method {@code getHighestPrecedenceSource(Source...)}
 * can be used as an auxiliary to determine the object to be used in a collection of
 * sources.
 * </p>
 * <p>
 * Additional metadata can also be defined for every configuration source, such as the
 * default configuration file name and the supplying object mapper, although some
 * parameters may not be available for all enumerated objects.
 * </p>
 * <p>
 * Enumerated objects may also provide methods for loading a configuration file into a
 * {@link GlobalConfiguration} object.
 * </p>
 *
 * @author oswaldo.bapvic.jr
 */
public enum Source
{

    /**
     * The source applicable to configuration data performed via the {@link Agent} annotation.
     */
    ANNOTATION(1, null, null)
    {
        @Override
        public Optional<GlobalConfiguration> loadGlobalConfigurationFile()
        {
            return Optional.empty();
        }
    },

    /**
     * The default source applicable to configuration data performed programmatically with no
     * specific source defined.
     */
    DEFAULT(2, null, null)
    {
        @Override
        public Optional<GlobalConfiguration> loadGlobalConfigurationFile()
        {
            return Optional.empty();
        }
    },

    /**
     * The source applicable to configuration data loaded from an XML file.
     */
    XML(3, "agents.xml", XmlMapper::new),

    /**
     * The source applicable for configuration data loaded from a JSON file.
     */
    JSON(4, "agents.json", JsonMapper::new);

    private static final Logger LOGGER = LoggerFactory.getLogger(Source.class);

    private final int precedence;
    private final String defaultFileName;
    private final Supplier<ObjectMapper> objectMapperSupplier;

    /**
     * Constructs a {@link Source} object with all parameters.
     *
     * @param precedence           an integer number representing the order of importance
     *                             given to a {@code Source} where a {@code Source} with a
     *                             higher value can take precedence over the other ones
     * @param defaultFileName      the default resource/file name from a particular source
     * @param objectMapperSupplier an {@link ObjectMapper} supplier for the configuration file
     */
    private Source(int precedence, String defaultFileName, Supplier<ObjectMapper> objectMapperSupplier)
    {
        this.precedence = precedence;
        this.defaultFileName = defaultFileName;
        this.objectMapperSupplier = objectMapperSupplier;
    }

    public Optional<GlobalConfiguration> loadGlobalConfigurationFile()
    {
        return loadGlobalConfigurationFile(defaultFileName);
    }

    public Optional<GlobalConfiguration> loadGlobalConfigurationFile(String fileName)
    {
        LOGGER.debug("Looking for global {} configuration file \"{}\"", this, fileName);
        URL url = Source.class.getClassLoader().getResource(fileName);
        if (url == null)
        {
            LOGGER.debug("Global {} configuration file \"{}\" not found", this, fileName);
            return Optional.empty();
        }
        LOGGER.debug("Global {} configuration file \"{}\" found", this, fileName);
        return loadGlobalConfigurationFile(url);
    }

    public Optional<GlobalConfiguration> loadGlobalConfigurationFile(URL url)
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

    public Optional<GlobalConfiguration> loadGlobalConfigurationFileQuietly()
    {
        return loadQuietly(this::loadGlobalConfigurationFile);
    }

    public Optional<GlobalConfiguration> loadGlobalConfigurationFileQuietly(String fileName)
    {
        return loadQuietly(() -> loadGlobalConfigurationFile(fileName));
    }

    private Optional<GlobalConfiguration> loadQuietly(Supplier<Optional<GlobalConfiguration>> supplier)
    {
        try
        {
            return supplier.get();
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
        return stream.filter(Objects::nonNull)
                     .sorted(Comparator.comparingInt(Source::getPrecedence).reversed())
                     .findFirst().orElse(DEFAULT);
    }

    /**
     * Returns the precedence level, i.e., an integer number representing the order of
     * importance given to a {@code Source}, where a {@code Source} with a higher value can
     * take precedence over the other ones.
     *
     * @return the precedence level for this source
     */
    public int getPrecedence()
    {
        return precedence;
    }

}
