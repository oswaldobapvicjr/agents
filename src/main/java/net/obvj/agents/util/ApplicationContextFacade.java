/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
