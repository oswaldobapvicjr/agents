package net.obvj.agents.util;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.obvj.agents.conf.AgentConfiguration;
import net.obvj.agents.exception.AgentConfigurationException;
import net.obvj.agents.test.agents.invalid.TestClassNotAgent;
import net.obvj.agents.test.agents.valid.DummyAgent;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeCronAndRunMethod;
import net.obvj.agents.test.agents.valid.TestAgentWithNoNameAndTypeTimerAndRunMethod;

/**
 * Unit tests for the {@link AnnotatedAgentScanner} class.
 *
 * @author oswaldo.bapvic.jr
 */
class AnnotatedAgentScannerTest
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
    void constructor_instantiationNotAllowed()
    {
        assertThat(AnnotatedAgentScanner.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void findAnnotatedAgentClasses_validPackage_expectedClasses()
    {
        Set<String> result = AnnotatedAgentScanner.findAnnotatedAgentClasses(AGENTS_TEST_PACKAGE);
        assertTrue(result.containsAll(EXPECTED_TEST_PACKAGE_AGENT_CLASSES),
                () -> "Not all expected agents were found");
        assertFalse(result.containsAll(UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES),
                () -> "Unexpected agent was found");
    }

    @Test
    void findAnnotatedAgentClasses_invalidPackage_emptySet()
    {
        assertThat(AnnotatedAgentScanner.findAnnotatedAgentClasses(INVALID_PACKAGE), equalTo(Collections.emptySet()));
    }

    @Test
    void scanPackage_validPackage_validAgentConfigurations()
    {
        Set<AgentConfiguration> agentConfigurationsFound = AnnotatedAgentScanner.scanPackage(AGENTS_TEST_PACKAGE);
        assertTrue(agentConfigurationsFound.size() > EXPECTED_TEST_PACKAGE_AGENT_CLASSES.size(),
                () -> "The expected number of agents was not reached");

        Set<String> mappedClassNames = agentConfigurationsFound.stream()
                .map(AgentConfiguration::getClassName)
                .collect(Collectors.toSet());
        assertTrue(mappedClassNames.containsAll(EXPECTED_TEST_PACKAGE_AGENT_CLASSES),
                () -> "Not all expected agents were found");
    }

    @Test
    void scanPackage_invalidPackage_emptySet()
    {
        assertThat(AnnotatedAgentScanner.scanPackage(INVALID_PACKAGE), equalTo(Collections.emptySet()));
    }

    @Test
    void toClass_invalidClass_exception()
    {
        assertThat(() -> AnnotatedAgentScanner.toClass("invalid"),
                throwsException(AgentConfigurationException.class)
                .withCause(ClassNotFoundException.class));
    }

}
