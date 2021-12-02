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

import java.util.function.BinaryOperator;

/**
 * This class provides common functions for use with Streams and Collectors.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.2.0
 */
public class Functions
{
    private Functions()
    {
        throw new IllegalStateException("Instantiation not allowed");
    }

    /**
     * A merger operation upon two operands of the same type, typically for handling
     * collisions between values associated with the same key in a Map, where the first object
     * is always applied.
     *
     * @param <T> the type of the operands and result of the operator
     * @return the first object, always
     */
    public static <T> BinaryOperator<T> firstWinsMerger()
    {
        return (first, last) -> first;
    }

    /**
     * A merger operation upon two operands of the same type, typically for handling
     * collisions between values associated with the same key in a Map, where the second
     * object is always applied.
     *
     * @param <T> the type of the operands and result of the operator
     * @return the second object, always
     */
    public static <T> BinaryOperator<T> lastWinsMerger()
    {
        return (first, last) -> last;
    }
}
