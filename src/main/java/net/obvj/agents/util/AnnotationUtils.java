package net.obvj.agents.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import net.obvj.agents.exception.InvalidClassException;

/**
 * Utility methods for working with annotations and package scanning.
 *
 * @author oswaldo.bapvic.jr
 */
public class AnnotationUtils
{
    private AnnotationUtils()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Enumerates applicable method filters.
     *
     * @author oswaldo.bapvic.jr
     */
    public enum MethodFilter
    {
        /**
         * A no-op filter.
         */
        DEFAULT
        {
            @Override
            Method filter(Method method)
            {
                return method;
            }
        },

        /**
         * A filter that returns an exception if the specified method contains parameters.
         */
        NO_PARAMETER
        {
            @Override
            Method filter(Method method)
            {
                Objects.requireNonNull(method, "The method must not be null");
                int parameterCount = method.getParameterCount();
                if (parameterCount != 0)
                {
                    throw Exceptions.invalidClass("The method \"%s\" has parameter(s).", method.getName());
                }
                return method;
            }
        };

        /**
         * Applies the filter to the specified method, returning the same object or throwing an
         * exception if the method does not pass the filter rule(s).
         *
         * @param method the method to be evaluated
         * @return the same {@link Method} specified at input if all rules match
         *
         * @throws InvalidClassException if the method does not pass the filter rule(s)
         */
        abstract Method filter(Method method);

    }

    /**
     * Returns the public method of the given class that is annotated with the given
     * annotation, provided that only a single method containing this annotation exists in the
     * class.
     * <p>
     * <strong>Note:</strong> This has the same effect as calling:
     * <p>
     * <code>
     * getSinglePublicMethodWithAnnotation(annotationClass, sourceClass, MethodFilter.DEFAULT);
     * </code>
     *
     * @param annotationClass the {@link Annotation} class that must be present on a method to
     *                        be matched, not null
     * @param sourceClass     the {@link Class} to query, not null
     *
     * @return a {@link Method} which is annotated with the specified annotation
     *
     * @throws NullPointerException  if either of the class parameters is null
     * @throws InvalidClassException if either no method or more than one method found
     */
    public static Method getSinglePublicMethodWithAnnotation(Class<? extends Annotation> annotationClass,
            Class<?> sourceClass)
    {
        return getSinglePublicMethodWithAnnotation(annotationClass, sourceClass, MethodFilter.DEFAULT);
    }

    /**
     * Returns the public method of the given class that is annotated with the given
     * annotation, provided that only a single method containing this annotation exists in the
     * class.
     *
     * @param annotationClass the {@link Annotation} class that must be present on a method to
     *                        be matched, not null
     * @param sourceClass     the {@link Class} to query, not null
     * @param methodFilter    the {@link MethodFilter} to apply; {@code null} is allowed and
     *                        it defaults to {@link MethodFilter#DEFAULT}
     *
     * @return a {@link Method} which is annotated with the specified annotation
     *
     * @throws NullPointerException  if either of the class parameters is null
     * @throws InvalidClassException if either no method or more than one method found, or if
     *                               the method does not match the rules of the specified
     *                               {@link MethodFilter}
     */
    public static Method getSinglePublicMethodWithAnnotation(Class<? extends Annotation> annotationClass,
            Class<?> sourceClass, MethodFilter methodFilter)
    {
        Objects.requireNonNull(annotationClass, "The annotation class must not be null");
        Objects.requireNonNull(sourceClass, "The source class must not be null");

        List<Method> agentTaskMethods = MethodUtils.getMethodsListWithAnnotation(sourceClass, annotationClass);

        if (agentTaskMethods.isEmpty())
        {
            throw Exceptions.invalidClass("No public method with the @%s annotation found in the class %s",
                    annotationClass.getSimpleName(), sourceClass.getName());
        }
        if (agentTaskMethods.size() > 1)
        {
            throw Exceptions.invalidClass(
                    "%s methods with the @%s annotation found in the class %s. Only one is allowed.",
                    agentTaskMethods.size(), annotationClass.getSimpleName(), sourceClass.getName());
        }

        return applyFilter(methodFilter, agentTaskMethods.get(0), annotationClass);
    }

    private static Method applyFilter(MethodFilter methodFilter, Method candidateMethod,
            Class<? extends Annotation> annotationClass)
    {
        try
        {
            MethodFilter localMethodFilter = ObjectUtils.defaultIfNull(methodFilter, MethodFilter.DEFAULT);
            return localMethodFilter.filter(candidateMethod);
        }
        catch (InvalidClassException exception)
        {
            throw Exceptions.invalidClass(exception, "The method contaning the @%s annotation is not valid.",
                    annotationClass.getSimpleName());
        }
    }

    /**
     * Scans the class path from a base package to find classes annotated with a given
     * annotation.
     *
     * @param annotationClass        the annotation to be filter
     * @param basePackage            the package to search for annotated classes
     *
     * @return a set of auto-detected class names; or an empty set
     */
    public static Set<String> findClassesWithAnnotation(Class<? extends Annotation> annotationClass, String basePackage)
    {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));

        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
        return candidateComponents.stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toSet());
    }
}
