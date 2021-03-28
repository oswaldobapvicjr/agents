package net.obvj.agents.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.util.AnnotationUtils;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.Duration;

/**
 * Contains methods for scanning package(s) to find annotated agents.
 *
 * @author oswaldo.bapvic.jr
 */
public class AnnotatedAgentScanner
{
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedAgentScanner.class);

    private AnnotatedAgentScanner()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @return a {@link Map} of {@link AgentConfiguration} by annotated class name
     */
    public static Map<String, AgentConfiguration> scanPackage(String basePackage)
    {
        Map<String, AgentConfiguration> agentsByClassName = new HashMap<>();
        Set<String> classNames = findAnnotatedAgentClasses(basePackage);
        Set<Class<?>> classes = classNames.stream().map(AnnotatedAgentScanner::toClass).collect(Collectors.toSet());
        classes.forEach(clazz -> agentsByClassName.put(clazz.getName(), AgentConfiguration.fromAnnotatedClass(clazz)));
        return agentsByClassName;
    }

    /**
     * @returns a list of candidate class names found in class path with the {@code @Agent}
     *          annotation
     */
    protected static Set<String> findAnnotatedAgentClasses(String basePackage)
    {
        LOG.info("Scanning package: {}", basePackage);

        Stopwatch stopwatch = Stopwatch.createStarted(Type.WALL_CLOCK_TIME);
        Set<String> classNames = AnnotationUtils.findClassesWithAnnotation(Agent.class, basePackage);

        Duration elapsedDuration = stopwatch.elapsedTime(Type.WALL_CLOCK_TIME);
        LOG.info("{} agent(s) found in {}: {}", classNames.size(), elapsedDuration, classNames);

        return classNames;
    }

    protected static Class<?> toClass(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException exception)
        {
            throw new AgentConfigurationException(exception);
        }
    }

}
