package net.obvj.agents.util.logging;

import static net.obvj.junit.utils.matchers.AdvancedMatchers.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

/**
 * Unit tests for the {@link LogUtils}.
 *
 * @author oswaldo.bapvic.jr
 * @since 0.3.0
 */
@ExtendWith(MockitoExtension.class)
class LogUtilsTest
{
    private static final String PATTERN1 = "pattern1{}";
    private static final String ARGUMENT1 = "argument1";
    private static final String REPLACEMENT = "X";
    private static final LogArgument LOG_ARGUMENT_LOGGABLE = new LogArgument("\\w*", ARGUMENT1);
    private static final LogArgument LOG_ARGUMENT_NOT_LOGGABLE = new LogArgument("[0-9]", ARGUMENT1, REPLACEMENT);

    @Mock
    private Logger logger;

    @Test
    void constructor_instantiationNotAllowed()
    {
        assertThat(LogUtils.class, instantiationNotAllowed().throwing(UnsupportedOperationException.class)
                .withMessage("Instantiation not allowed"));
    }

    @Test
    void logInfoSafely_infoLoggableAndValidArgument_loggableArgument()
    {
        when(logger.isInfoEnabled()).thenReturn(true);
        LogUtils.logInfoSafely(logger, PATTERN1, LOG_ARGUMENT_LOGGABLE);
        verify(logger).info(PATTERN1, new Object[] { ARGUMENT1 });
    }

    @Test
    void logInfoSafely_infoLoggableAndInvalidArgument_replacementString()
    {
        when(logger.isInfoEnabled()).thenReturn(true);
        LogUtils.logInfoSafely(logger, PATTERN1, LOG_ARGUMENT_NOT_LOGGABLE);
        verify(logger).info(PATTERN1, new Object[] { REPLACEMENT });
    }

    @Test
    void logInfoSafely_infoNotLoggable_noAction()
    {
        when(logger.isInfoEnabled()).thenReturn(false);
        LogUtils.logInfoSafely(logger, PATTERN1, LOG_ARGUMENT_LOGGABLE);
        verifyNoMoreInteractions(logger);
    }

}
