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

package net.obvj.agents.conf;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * An object that parses and stores global configuration data.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.3.0
 */
public class GlobalConfiguration
{

    private int maxAgentHistorySize;

    /**
     * @return the maxAgentHistorySize
     */
    public int getMaxAgentHistorySize()
    {
        return maxAgentHistorySize;
    }

    /**
     * Generates a string representation of this {@link GlobalConfiguration}.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("maxAgentHistorySize", maxAgentHistorySize)
                .build();
    }

}
