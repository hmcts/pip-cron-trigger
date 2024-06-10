package uk.gov.hmcts.reform.pip.cron.trigger.runner;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pip.cron.trigger.config.CronTimerProperties;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.AccountInactiveVerificationTrigger;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.Trigger;

import java.util.List;
import java.util.stream.Stream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleRunnerTest {

    @InjectMocks
    ScheduleRunner scheduleRunner;

    @Mock
    private List<Trigger> mockArrayList;

    @Mock
    CronTimerProperties cronTimerProperties;

    @Mock
    AccountInactiveVerificationTrigger accountInactiveVerificationTrigger;

    private static final String MESSAGE_DO_NOT_MATCH_MESSAGE = "Messages do not match";
    private static final String STATUS_DO_NOT_MATCH_MESSAGE = "Status codes do not match";

    @Test
    void testRunnerWhereInvalidArgProvided() throws Exception {
        try (LogCaptor logCaptor = LogCaptor.forClass(ScheduleRunner.class)) {

            when(cronTimerProperties.getTriggerType()).thenReturn("UNKNOWN_ENUM");

            int statusCode = catchSystemExit(() -> {
                scheduleRunner.run();
            });

            assertTrue(
                logCaptor.getErrorLogs().get(0).contains("Invalid or no schedule type set. Exiting"),
                MESSAGE_DO_NOT_MATCH_MESSAGE
            );

            assertEquals(1, statusCode, STATUS_DO_NOT_MATCH_MESSAGE);
        }
    }

    @Test
    void testRunnerWhereSuccessfullyTriggered() {
        when(mockArrayList.stream()).thenReturn(Stream.of(accountInactiveVerificationTrigger));
        when(accountInactiveVerificationTrigger.isApplicable(ScheduleTypes.ACCOUNT_INACTIVE_VERIFICATION))
            .thenReturn(true);
        doNothing().when(accountInactiveVerificationTrigger).trigger();

        when(cronTimerProperties.getTriggerType()).thenReturn("ACCOUNT_INACTIVE_VERIFICATION");

        scheduleRunner.run();
        verify(accountInactiveVerificationTrigger, Mockito.times(1)).trigger();
    }

    @Test
    void testRunnerWhereCouldNotFindTrigger() throws Exception {
        try (LogCaptor logCaptor = LogCaptor.forClass(ScheduleRunner.class)) {
            when(mockArrayList.stream()).thenReturn(Stream.of(accountInactiveVerificationTrigger));
            when(accountInactiveVerificationTrigger.isApplicable(ScheduleTypes.NO_MATCH_ARTEFACTS))
                .thenReturn(false);

            when(cronTimerProperties.getTriggerType()).thenReturn("NO_MATCH_ARTEFACTS");

            int statusCode = catchSystemExit(() -> {
                scheduleRunner.run();
            });

            assertTrue(logCaptor.getErrorLogs().get(0).contains("Failed to find trigger. Exiting"),
                       MESSAGE_DO_NOT_MATCH_MESSAGE);

            assertEquals(1, statusCode, STATUS_DO_NOT_MATCH_MESSAGE);

        }
    }

}
