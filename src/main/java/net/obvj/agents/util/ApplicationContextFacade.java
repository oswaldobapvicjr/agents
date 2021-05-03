package net.obvj.agents.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * A facade for the Application Context holding the components required by this project.
 *
 * @author oswaldo.bapvic.jr
 */
public class ApplicationContextFacade
{
    private static ApplicationContext context = new AnnotationConfigApplicationContext("net.obvj.agents");

    private ApplicationContextFacade()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * Return the bean instance that uniquely matches the given object type.
     *
     * @param <T>       the bean type to return
     * @param beanClass the class which bean is to be returned; it can be an interface or
     *                  superclass
     * @return an instance of the single bean matching the required type
     */
    public static <T> T getBean(Class<T> beanClass)
    {
        return context.getBean(beanClass);
    }
}
