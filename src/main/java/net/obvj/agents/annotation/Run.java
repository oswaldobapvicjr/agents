package net.obvj.agents.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies the annotated method as the one to be executed for a class annotated with
 * {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Run
{
    // Marker annotation
}
