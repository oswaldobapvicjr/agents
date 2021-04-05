package net.obvj.agents.conf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Test;

import net.obvj.agents.test.agents.invalid.TestClassNotAgent;
import net.obvj.agents.test.agents.valid.DummyAgent;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeCronAndRunMethod;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeTimerAndRunMethod;

/**
 * Unit tests for the {@link AnnotatedAgentScanner} class.
 *
 * @author oswaldo.bapvic.jr
 */
class AnnotatedAgentsTest
{
    private static final String AGENTS_TEST_PACKAGE = "net.obvj.agents.test.agents";
    private static final String INVALID_PACKAGE = "invalid";

    private static final List<String> EXPECTED_TEST_PACKAGE_AGENT_CLASSES = Arrays
            .asList(DummyAgent.class.getName(),
                    TestAgentWithNoNameAndTypeCronAndRunMethod.class.getName(),
                    TestAgentWithNoNameAndTypeTimerAndRunMethod.class.getName());

    private static final List<String> UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES = Arrays
            .asList(TestClassNotAgent.class.getName());

    @Test
    void findAnnotatedAgentClasses_validPackage_expectedClasses()
    {
        Set<String> result = AnnotatedAgentScanner.findAnnotatedAgentClasses(AGENTS_TEST_PACKAGE);
        assertTrue("Not all expected agents were found", result.containsAll(EXPECTED_TEST_PACKAGE_AGENT_CLASSES));
        assertFalse("Unexpected agent was found", result.containsAll(UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES));
    }

    @Test
    void findAnnotatedAgentClasses_invalidPackage_emptySet()
    {
        assertThat(AnnotatedAgentScanner.findAnnotatedAgentClasses(INVALID_PACKAGE), equalTo(Collections.emptySet()));
    }

}
