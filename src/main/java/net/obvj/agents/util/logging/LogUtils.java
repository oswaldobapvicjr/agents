/*
 * Copyright 2022 obvj.net
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

package net.obvj.agents.util.logging;

import static java.util.Arrays.stream;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.slf4j.Logger;

/**
 * Utility methods for logging.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.3.0
 */
public class LogUtils
{

    /**
     * Private constructor to avoid instantiation
     */
    private LogUtils()
    {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }

    public static void logInfoSafely(Logger logger, String pattern, LogArgument... arguments)
    {
        logSafely(Logger::isInfoEnabled, logger::info, logger, pattern, arguments);
    }

    public static void logWarnSafely(Logger logger, String pattern, LogArgument... arguments)
    {
        logSafely(Logger::isWarnEnabled, logger::warn, logger, pattern, arguments);
    }

    private static void logSafely(Predicate<Logger> levelPredicate, BiConsumer<String, Object[]> loggingFunction,
            Logger logger, String pattern, LogArgument... arguments)
    {
        if (levelPredicate.test(logger))
        {
            Object[] loggableArguments = stream(arguments).map(LogArgument::getLoggableArgument).toArray(Object[]::new);
            loggingFunction.accept(pattern, loggableArguments);
        }
    }
}
