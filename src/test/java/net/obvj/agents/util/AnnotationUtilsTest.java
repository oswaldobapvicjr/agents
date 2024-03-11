package net.obvj.agents.util;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static net.obvj.junit.utils.matchers.AdvancedMatchers.throwsException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.obvj.agents.annotation.Agent;
import net.obvj.agents.annotation.Run;
import net.obvj.agents.exception.InvalidClassException;
import net.obvj.agents.test.agents.invalid.*;
import net.obvj.agents.test.agents.valid.*;
import net.obvj.agents.util.AnnotationUtils.MethodFilter;

/**
 * Unit tests for the {@link AnnotationUtils}.
 *
 * @author oswaldo.bapvic.jr
 */
class AnnotationUtilsTest
{
    private static final String ALL_TEST_AGENTS_PACKAGE = "net.obvj.agents.test.agents";
    private static final String VALID_TEST_AGENTS_PACKAGE = "net.obvj.agents.test.agents.valid";

    private static final List<String> VALID_AGENT_CLASS_NAMES = Arrays.asList(DummyAgent.class.getName(),
            TestAgentWithNoNameAndTypeCronAndRunMethod.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndRunMethod.class.getName(),
            TestTimerAgent1.class.getName(),
            TestTimerAgentThrowingException.class.getName());

    private static final List<String> INVALID_AGENT_CLASS_NAMES = Arrays.asList(
            TestAgentWithAllCustomParams.class.getName(),
            TestAgentWithAllCustomParamsAndConstructorThrowsException.class.getName(),
            TestAgentWithAllCustomParamsAndPrivateConstructor.class.getName(),
            TestAgentWithAllCustomParamsAndPrivateRunMethod.class.getName(),
            TestAgentWithCustomNameAndType.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndNoRunMethod.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndTwoRunMethods.class.getName(),
            TestAgentWithNoType.class.getName(),
            TestAgentWithTwoRunMethods.class.getName());

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
        assertThat(AnnotationUtils.class, instantiationNotAllowed().throwing(IllegalStateException.class));
    }

    @Test
    void findClassesAnnotation_agentAndCommonPackage_allAgentsFound()
    {
        Set<String> classNames = AnnotationUtils.findClassesWithAnnotation(Agent.class, ALL_TEST_AGENTS_PACKAGE);

        assertThat("Unexpected number of classes found", classNames.size(), equalTo(ALL_AGENT_CLASS_NAMES.size()));

        assertTrue(classNames.containsAll(ALL_AGENT_CLASS_NAMES));

        assertFalse(classNames.containsAll(UNEXPECTED_AGENT_CLASS_NAMES),
                () -> "A class without the @Agent annotation should not be retrieved");
    }

    @Test
    void findClassesAnnotation_agentAndPackageContainingValidAgentsOnly_allAgentsFound()
    {
        Set<String> classNames = AnnotationUtils.findClassesWithAnnotation(Agent.class, VALID_TEST_AGENTS_PACKAGE);

        assertThat("Unexpected number of classes found", classNames.size(), equalTo(VALID_AGENT_CLASS_NAMES.size()));

        assertTrue(classNames.containsAll(VALID_AGENT_CLASS_NAMES));

        assertFalse(classNames.containsAll(INVALID_AGENT_CLASS_NAMES),
                () -> "A class from another package should not be retrieved");
    }

    @Test
    void getSinglePublicMethodWithAnnotation_noParamFilterAndsingleMatchingMethod_success()
    {
        Method method = AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class, DummyAgent.class,
                MethodFilter.NO_PARAMETER);
        assertThat(method.getName(), is(equalTo("runTask")));
        assertThat(method.getParameterCount(), is(equalTo(0)));
    }

    @Test
    void getSinglePublicMethodWithAnnotation_noParamFilterAndTwoMatchingMethods_exception()
    {
        assertThat(() -> AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class,
                TestAgentWithTwoRunMethods.class, MethodFilter.NO_PARAMETER),
                throwsException(InvalidClassException.class));
    }

    @Test
    void getSinglePublicMethodWithAnnotation_noRunMethod_exception()
    {
        assertThat(() -> AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class,
                TestAgentWithAllCustomParams.class), throwsException(InvalidClassException.class));
    }

    @Test
    void getSinglePublicMethodWithAnnotation_privateRunMethod_exception()
    {
        assertThat(() -> AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class, TestAgentWithNoType.class),
                throwsException(InvalidClassException.class));
    }

    @Test
    void getSinglePublicMethodWithAnnotation_runMethodWithParameter_success()
    {
        Method method = AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class,
                TestAgentWithCustomNameAndType.class);
        assertThat(method.getName(), is(equalTo("run")));
        assertThat(method.getParameterCount(), is(equalTo(1)));
    }

    @Test
    void getSinglePublicMethodWithAnnotation_noParamFilterAndRunMethodWithParameter_exception()
    {
        assertThat(
                () -> AnnotationUtils.getSinglePublicMethodWithAnnotation(Run.class,
                        TestAgentWithCustomNameAndType.class, MethodFilter.NO_PARAMETER),
                throwsException(InvalidClassException.class));
    }

}
