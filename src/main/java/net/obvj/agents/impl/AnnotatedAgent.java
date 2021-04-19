package net.obvj.agents.impl;

import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.util.ReflectionUtils;

import net.obvj.agents.annotation.Run;
import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.exception.InvalidClassException;
import net.obvj.agents.util.AnnotationUtils;
import net.obvj.agents.util.AnnotationUtils.MethodFilter;

/**
 * An object that prepares and holds the required metadata and infrastructure for the
 * execution of an object annotated as {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 */
public class AnnotatedAgent
{
    private final Class<?> agentClass;
    private final Method agentTaskMethod;
    private final Object agentInstance;

    /**
     * Validates annotations and prepares all objects for execution.
     *
     * @param configuration the {@link AgentConfiguration} to be parsed
     * @throws AgentConfigurationException if any exception regarding a reflective operation
     *                                     (e.g.: class or method not found) occurs
     */
    public AnnotatedAgent(AgentConfiguration configuration)
    {
        try
        {
            String agentClassName = configuration.getClassName();
            agentClass = Class.forName(agentClassName);
            agentTaskMethod = AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class, agentClass,
                    MethodFilter.NO_PARAMETER);
            agentInstance = ConstructorUtils.invokeConstructor(agentClass);
        }
        catch (ReflectiveOperationException cause)
        {
            throw new InvalidClassException(cause);
        }
    }

    /**
     * Invokes the method annotated as {@code @Run} for the agent.
     */
    public void runAgentTask()
    {
        ReflectionUtils.invokeMethod(agentTaskMethod, agentInstance);
    }

    /**
     * @return the agentClass
     */
    public Class<?> getAgentClass()
    {
        return agentClass;
    }

    /**
     * @return the annotated agent's runnable method
     */
    public Method getRunMethod()
    {
        return agentTaskMethod;
    }

    /**
     * @return the annotated agent's instance
     */
    public Object getAgentInstance()
    {
        return agentInstance;
    }

}
