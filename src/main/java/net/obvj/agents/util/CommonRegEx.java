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

package net.obvj.agents.util;

/**
 * Enumerates common Regular Expressions for use.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.3.0
 */
public class CommonRegEx
{
    private CommonRegEx()
    {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }

    /**
     * A regex for valid Java package names
     */
    public static final String JAVA_PACKAGE_NAME = "^[a-z]+(\\.[a-z0-9]+)*$";

}
