package net.obvj.agents.conf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;

import net.obvj.agents.AgentType;
import net.obvj.agents.annotation.Agent;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.util.Exceptions;

/**
 * An object that contains the set-up of an agent.
 *
 * @author oswaldo.bapvic.jr
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentConfiguration
{
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "type")
    private AgentType type;

    @XmlElement(name = "class")
    private String agentClass;

    @XmlElement(name = "interval")
    private String interval;

    public AgentConfiguration()
    {
        // Required by JAXB to create an AgentConfiguration from XML
    }

    private AgentConfiguration(Builder builder)
    {
        this.name = builder.name;
        this.type = builder.type;
        this.agentClass = builder.agentClass;
        this.interval = builder.interval;
    }

    public String getName()
    {
        return name;
    }

    public AgentType getType()
    {
        return type;
    }

    public String getAgentClass()
    {
        return agentClass;
    }

    public String getInterval()
    {
        return interval;
    }

    /**
     * An {@link AgentConfiguration} builder.
     *
     * @author oswaldo.bapvic.jr
     */
    public static class Builder
    {
        private String name;
        private AgentType type;
        private String agentClass;
        private String interval;

        public Builder(AgentType type)
        {
            this.type = type;
        }

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Builder agentClass(String agentClass)
        {
            this.agentClass = agentClass;
            return this;
        }

        public Builder interval(String interval)
        {
            this.interval = interval;
            return this;
        }

        public AgentConfiguration build()
        {
            if (StringUtils.isEmpty(name))
            {
                throw new IllegalStateException("name cannot be empty");
            }
            if (type == null)
            {
                throw new AgentConfigurationException("type cannot be null");
            }
            if (StringUtils.isEmpty(agentClass))
            {
                throw new AgentConfigurationException("agentClass cannot be null");
            }
            if (StringUtils.isEmpty(interval))
            {
                interval = type.getDefaultInterval();
            }
            return new AgentConfiguration(this);
        }

    }


    public static AgentConfiguration fromAnnotatedClass(Class<?> clazz)
    {
        Agent annotation = clazz.getAnnotation(Agent.class);
        if (annotation == null)
        {
            throw Exceptions.agentConfiguration("@Agent annotation is not present in class %s", clazz);
        }

        // Name: If not specified in annotation, then use the class canonical name
        String name = StringUtils.defaultIfEmpty(annotation.name(), clazz.getCanonicalName());

        AgentType type = annotation.type();
        String agentClass = clazz.getCanonicalName();
        String frequency = annotation.frequency();

        Builder builder = new Builder(type).name(name).agentClass(agentClass).interval(frequency);
        return builder.build();
    }

    @Override
    public String toString()
    {
        return new StringBuilder("AgentConfiguration (class=").append(agentClass).append(")").toString();
    }

}
