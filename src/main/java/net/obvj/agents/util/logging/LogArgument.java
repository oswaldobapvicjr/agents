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

import java.util.function.Predicate;

/**
 * Represents a log argument that is subject to validation and omission or replacement
 * before actual logging, typically for security purposes.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.3.0
 */
public class LogArgument
{
    private final Predicate<String> predicate;
    private final String original;
    private final String replacement;

    public LogArgument(String pattern, String original)
    {
        this(pattern, original, "<?>");
    }

    public LogArgument(String pattern, String original, String replacement)
    {
        this(str -> str.matches(pattern), original, replacement);
    }

    private LogArgument(Predicate<String> predicate, String original, String replacement)
    {
        this.predicate = predicate;
        this.original = original;
        this.replacement = replacement;
    }

    public String getLoggableArgument()
    {
        return predicate.test(original) ? original : replacement;
    }

}
