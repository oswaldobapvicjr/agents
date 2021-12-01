package net.obvj.agents.conf;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
    private boolean modulate;

    protected AgentConfiguration(Builder builder)
    {
        this.name = builder.name;
        this.type = builder.type;
        this.className = builder.className;
        this.interval = builder.interval;
        this.modulate = builder.modulate;
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

    public boolean isModulate()
    {
        return modulate;
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

        @JsonProperty
        private boolean modulate;

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

        public Builder modulate(boolean modulate)
        {
            this.modulate = modulate;
            return this;
        }

        public AgentConfiguration build()
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
            return new AgentConfiguration(this);
        }

        /**
         * Returns a string representation of the target {@link AgentConfiguration}.
         *
         * @return a string representation of the target {@link AgentConfiguration}
         */
        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                    .append("name", name)
                    .append("className", className)
                    .append("type", type)
                    .append("interval", interval)
                    .append("modulate", modulate)
                    .toString();
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
        boolean modulate = annotation.modulate();

        Builder builder = new Builder().type(type).name(name).className(className).interval(interval)
                .modulate(modulate);
        return builder.build();
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
                .append("modulate", modulate)
                .build();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, className, interval, type, modulate);
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
                && Objects.equals(name, other.name) && type == other.type && modulate == other.modulate;
    }

}
