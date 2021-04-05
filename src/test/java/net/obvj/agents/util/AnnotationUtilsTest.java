package net.obvj.agents.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.test.agents.invalid.*;
import net.obvj.agents.test.agents.valid.*;

/**
 * Unit tests for the {@link AnnotationUtils}
 *
 * @author oswaldo.bapvic.jr
 */
class AnnotationUtilsTest
{
    private static final String ALL_TEST_AGENTS_PACKAGE = "net.obvj.agents.test.agents";
    private static final String VALID_TEST_AGENTS_PACKAGE = "net.obvj.agents.test.agents.valid";

    private static final List<String> VALID_AGENT_CLASS_NAMES = Arrays.asList(
            DummyAgent.class.getName(),
            TestAgentWithNoNameAndTypeCronAndRunMethod.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndRunMethod.class.getName(),
            TestTimerAgent1.class.getName());

    private static final List<String> INVALID_AGENT_CLASS_NAMES = Arrays.asList(
            TestAgentWithAllCustomParams.class.getName(),
            TestAgentWithAllCustomParamsAndPrivateConstructor.class.getName(),
            TestAgentWithAllCustomParamsAndPrivateRunMethod.class.getName(),
            TestAgentWithCustomNameAndType.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndNoRunMethod.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndTwoRunMethods.class.getName(),
            TestAgentWithNoType.class.getName());

    private static final List<String> UNEXPECTED_AGENT_CLASS_NAMES = Arrays.asList(TestClassNotAgent.class.getName());

    private static final List<String> ALL_AGENT_CLASS_NAMES = new ArrayList<>(
            VALID_AGENT_CLASS_NAMES.size() + INVALID_AGENT_CLASS_NAMES.size());

    static
    {
        ALL_AGENT_CLASS_NAMES.addAll(VALID_AGENT_CLASS_NAMES);
        ALL_AGENT_CLASS_NAMES.addAll(INVALID_AGENT_CLASS_NAMES);
    }


    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(AnnotationUtils.class, instantiationNotAllowed());
    }

    @Test
    void findClassesAnnotation_agentAndCommonPackage_allAgentsFound()
    {
        Set<String> classNames = AnnotationUtils.findClassesWithAnnotation(Agent.class, ALL_TEST_AGENTS_PACKAGE);

        assertThat("Unexpected number of classes found", classNames.size(), equalTo(ALL_AGENT_CLASS_NAMES.size()));

        assertTrue(classNames.containsAll(ALL_AGENT_CLASS_NAMES));

        assertFalse("A class without the @Agent annotation should not be retrieved",
                classNames.containsAll(UNEXPECTED_AGENT_CLASS_NAMES));
    }

    @Test
    void findClassesAnnotation_agentAndPackageContainingValidAgentsOnly_allAgentsFound()
    {
        Set<String> classNames = AnnotationUtils.findClassesWithAnnotation(Agent.class, VALID_TEST_AGENTS_PACKAGE);

        assertThat("Unexpected number of classes found", classNames.size(), equalTo(VALID_AGENT_CLASS_NAMES.size()));

        assertTrue(classNames.containsAll(VALID_AGENT_CLASS_NAMES));

        assertFalse("A class from another package should not be retrieved",
                classNames.containsAll(INVALID_AGENT_CLASS_NAMES));
    }

}
