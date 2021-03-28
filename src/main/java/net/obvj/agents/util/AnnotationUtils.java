package net.obvj.agents.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import net.obvj.agents.exception.AgentConfigurationException;

public class AnnotationUtils
{
    private AnnotationUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns the method of the given class that is annotated with the given annotation,
     * provided that only a single method containing this annotation exists in the class.
     *
     * @param class           the {@link Class} to query
     * @param annotationClass the annotation that must be present on a method to be matched
     * @return a {@link Method}
     * @throws AgentConfigurationException if either no method or more than one method found
     */
    public static Method getSingleMethodWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
    {
        List<Method> agentTaskMethods = MethodUtils.getMethodsListWithAnnotation(clazz, annotationClass);

        if (agentTaskMethods.isEmpty())
        {
            throw Exceptions.agentConfiguration("No public method with @%s annotation found in the class %s",
                    annotationClass.getSimpleName(), clazz.getName());
        }
        if (agentTaskMethods.size() > 1)
        {
            throw Exceptions.agentConfiguration(
                    "%s methods with @%s annotation found in the class %s. Only one is allowed.",
                    agentTaskMethods.size(), annotationClass.getSimpleName(), clazz.getName());
        }
        return agentTaskMethods.get(0);
    }

    /**
     * Scans the class path from a base package to find classes annotated with a given
     * annotation.
     *
     * @param annotationClass        the annotation to be filter
     * @param basePackage            the package to check for annotated classes
     * @param additionalBasePackages (optional) additional base packages to check
     * @return a set of auto-detected class names
     */
    public static Set<String> findClassesWithAnnotation(Class<? extends Annotation> annotationClass, String basePackage)
    {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));

        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
        return candidateComponents.stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toSet());
    }
}
