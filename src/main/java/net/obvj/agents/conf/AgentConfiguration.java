package net.obvj.agents.conf;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.util.Exceptions;

/**
 * An object that parses and stores the configuration data for a particular agent.
 *
 * @author oswaldo.bapvic.jr
 */
public class AgentConfiguration
{
    private String name;
    private AgentType type;
    private String className;
    private String interval;
    private Source source;

    protected AgentConfiguration(Builder builder, Source source)
    {
        this.name = builder.name;
        this.type = builder.type;
        this.className = builder.className;
        this.interval = builder.interval;
        this.source = source;
    }

    public String getName()
    {
        return name;
    }

    public AgentType getType()
    {
        return type;
    }

    public String getClassName()
    {
        return className;
    }

    public String getInterval()
    {
        return interval;
    }

    public Source getSource()
    {
        return source;
    }

    /**
     * An {@link AgentConfiguration} builder.
     *
     * @author oswaldo.bapvic.jr
     */
    public static class Builder
    {
        protected static final String MSG_TYPE_CANNOT_BE_NULL = "the agent type cannot be null";
        protected static final String MSG_CLASS_NAME_CANNOT_BE_NULL = "the class name cannot be null";

        @JsonProperty
        private String name;

        @JsonProperty
        private AgentType type;

        @JsonProperty("class")
        private String className;

        @JsonProperty
        private String interval;

        public Builder type(AgentType type)
        {
            this.type = type;
            return this;
        }

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Builder className(String className)
        {
            this.className = className;
            return this;
        }

        public Builder interval(String interval)
        {
            this.interval = interval;
            return this;
        }

        public AgentConfiguration build()
        {
            return build(Source.DEFAULT);
        }

        protected AgentConfiguration build(Source source)
        {
            Objects.requireNonNull(type, MSG_TYPE_CANNOT_BE_NULL);

            if (StringUtils.isEmpty(className))
            {
                throw new AgentConfigurationException(MSG_CLASS_NAME_CANNOT_BE_NULL);
            }
            if (StringUtils.isEmpty(name))
            {
                name = StringUtils.defaultIfEmpty(name, className);
            }
            if (StringUtils.isEmpty(interval))
            {
                interval = type.getDefaultInterval();
            }
            return new AgentConfiguration(this, ObjectUtils.defaultIfNull(source, Source.DEFAULT));
        }

    }

    /**
     * Parses the agent configuration by checking the {@link Agent} annotation in the
     * specified class.
     *
     * @param agentClass the class to be mapped
     * @return an {@link AgentConfiguration} mapped from the specified source class
     * @throws AgentConfigurationException if the annotation is not present in the class
     */
    public static AgentConfiguration fromAnnotatedClass(Class<?> agentClass)
    {
        Agent annotation = agentClass.getAnnotation(Agent.class);
        if (annotation == null)
        {
            throw Exceptions.agentConfiguration("@Agent annotation is not present in class %s", agentClass);
        }

        // Name: If not specified in annotation, then use the class canonical name
        String name = StringUtils.defaultIfEmpty(annotation.name(), agentClass.getCanonicalName());

        AgentType type = annotation.type();
        String className = agentClass.getCanonicalName();
        String interval = annotation.interval();

        Builder builder = new Builder().type(type).name(name).className(className).interval(interval);
        return builder.build(Source.ANNOTATION);
    }

    /**
     * Generates a string representation of this AgentConfiguration.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("name", name)
                .append("className", className)
                .append("type", type)
                .append("interval", interval)
                .append("source", source)
                .build();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, className, interval, type, source);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null)
        {
            return false;
        }
        if (getClass() != object.getClass())
        {
            return false;
        }
        AgentConfiguration other = (AgentConfiguration) object;
        return Objects.equals(className, other.className) && Objects.equals(interval, other.interval)
                && Objects.equals(name, other.name) && source == other.source && type == other.type;
    }

    /**
     * Returns the AgentConfiguration with the highest precedence level among the specified
     * objects.
     * <p>
     * <strong>NOTE:</strong> This method is mainly intended for test purposes.
     *
     * @param agentConfigurations an array of AgentConfiguration objects to be evaluated
     * @return the object with the highest precedence level among the specified ones
     *
     * @throws IllegalArgumentException if no AgentConfiguratin object is specified
     */
    protected static AgentConfiguration getHighestPrecedenceConfiguration(AgentConfiguration... agentConfigurations)
    {
        return getHighestPrecedenceConfiguration(Arrays.asList(agentConfigurations));
    }

    /**
     * Returns the AgentConfiguration with the highest precedence level among the objects in
     * the specified {@link Collection}.
     * <p>
     * <strong>IMPORTANT:</strong> Although it is assumed that all agents contained in the
     * specified collection have the same class name, this method performs no validation on
     * this requirement. In other words, the object with higher precedence among the other
     * ones will be returned regardless of any other agent attribute.
     *
     * @param agentConfigurations a collection of AgentConfiguration objects to be evaluated
     * @return the object with the highest precedence level among the specified ones
     *
     * @throws IllegalArgumentException if an empty or null collection is received, or the
     *                                  specified collection does not contain at least one
     *                                  non-null object
     */
    public static AgentConfiguration getHighestPrecedenceConfiguration(
            Collection<AgentConfiguration> agentConfigurations)
    {
        if (CollectionUtils.isEmpty(agentConfigurations))
        {
            throw Exceptions.illegalArgument("At least one agent configuration must be specified");
        }

        if (agentConfigurations.size() > 1)
        {
            Source highestPrecedenceSource = getHighestPrecedenceSource(agentConfigurations);
            Optional<AgentConfiguration> optional = agentConfigurations.stream()
                    .filter(Objects::nonNull)
                    .filter(agent -> agent.getSource() == highestPrecedenceSource)
                    .findFirst();

            if (optional.isPresent())
            {
                return optional.get();
            }
        }

        // At this point, either the collection contains a single element only,
        // or the fist element could be returned as fall-back
        return requireNonNullOrThrow(agentConfigurations.iterator().next(),
                () -> Exceptions.illegalArgument("At least one non-null agent configuration must be specified"));
    }

    private static Source getHighestPrecedenceSource(Collection<AgentConfiguration> agentConfigurations)
    {
        return Source.getHighestPrecedenceSource(
                agentConfigurations.stream()
                                   .filter(Objects::nonNull)
                                   .map(AgentConfiguration::getSource));
    }

    private static <T> T requireNonNullOrThrow(T object, Supplier<? extends RuntimeException> exception)
    {
        if (object == null)
        {
            throw exception.get();
        }
        return object;
    }

}
