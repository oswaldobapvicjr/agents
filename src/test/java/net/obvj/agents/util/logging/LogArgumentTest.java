package net.obvj.agents.util.logging;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.obvj.agents.util.CommonRegEx;

class LogArgumentTest
{
    @Test
    void getLoggableargument_matchingRegex_originalString()
    {
        assertEquals("package", new LogArgument(CommonRegEx.JAVA_PACKAGE_NAME, "package").getLoggableArgument());
    }

    @Test
    void getLoggableargument_notMatchingRegex_replacementString()
    {
        assertEquals("?", new LogArgument(CommonRegEx.JAVA_PACKAGE_NAME, "${evilString}", "?").getLoggableArgument());
    }


}
