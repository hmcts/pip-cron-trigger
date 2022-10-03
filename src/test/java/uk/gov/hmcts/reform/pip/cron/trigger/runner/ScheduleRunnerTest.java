package uk.gov.hmcts.reform.pip.cron.trigger.runner;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.AccountInactiveVerificationTrigger;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.Trigger;

import java.util.ArrayList;
import java.util.stream.Stream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleRunnerTest {

    @InjectMocks
    ScheduleRunner scheduleRunner;

    @Mock
    private ArrayList<Trigger> mockArrayList;

    @Spy
    AccountInactiveVerificationTrigger accountInactiveVerificationTrigger;

    @Test
    void testRunnerWhereNoArgProvided() throws Exception {
        try (LogCaptor logCaptor = LogCaptor.forClass(ScheduleRunner.class)) {
            int statusCode = catchSystemExit(() -> {
                scheduleRunner.run();
            });

            assertTrue(logCaptor.getErrorLogs().get(0).contains("Invalid or no argument passed in. Exiting"),
                       "Messages do not match");

            assertEquals(1, statusCode, "Status codes do not match");

        }
    }

    @Test
    void testRunnerWhereInvalidArgProvided() throws Exception {
        try (LogCaptor logCaptor = LogCaptor.forClass(ScheduleRunner.class)) {
            int statusCode = catchSystemExit(() -> {
                scheduleRunner.run("UNKNOWN_ENUM");
            });

            assertTrue(
                logCaptor.getErrorLogs().get(0).contains("Invalid or no argument passed in. Exiting"),
                "Messages do not match"
            );

            assertEquals(1, statusCode, "Status codes do not match");
        }
    }

    @Test
    void testRunnerWhereMoreThanOneArgProvided() throws Exception {
        try (LogCaptor logCaptor = LogCaptor.forClass(ScheduleRunner.class)) {
            int statusCode = catchSystemExit(() -> {
                scheduleRunner.run("MEDIA_APPLICATION_REPORTING", "EXPIRED_ARTEFACTS");
            });

            assertTrue(logCaptor.getErrorLogs().get(0).contains("Invalid or no argument passed in. Exiting"),
                       "Messages do not match");

            assertEquals(1, statusCode, "Status codes do not match");

        }
    }

    @Test
    void testRunnerWhereSuccessfullyTriggered() {
        when(mockArrayList.stream()).thenReturn(Stream.of(accountInactiveVerificationTrigger));
        when(accountInactiveVerificationTrigger.isApplicable(ScheduleTypes.ACCOUNT_INACTIVE_VERIFICATION))
            .thenReturn(true);
        doNothing().when(accountInactiveVerificationTrigger).trigger();

        scheduleRunner.run("ACCOUNT_INACTIVE_VERIFICATION");
        verify(accountInactiveVerificationTrigger, Mockito.times(1)).trigger();
    }

    @Test
    void testRunnerWhereCouldNotFindTrigger() throws Exception {
        try (LogCaptor logCaptor = LogCaptor.forClass(ScheduleRunner.class)) {
            when(mockArrayList.stream()).thenReturn(Stream.of(accountInactiveVerificationTrigger));
            when(accountInactiveVerificationTrigger.isApplicable(ScheduleTypes.NO_MATCH_ARTEFACTS))
                .thenReturn(false);

            int statusCode = catchSystemExit(() -> {
                scheduleRunner.run("NO_MATCH_ARTEFACTS");
            });

            assertTrue(logCaptor.getErrorLogs().get(0).contains("Failed to find trigger. Exiting"),
                       "Messages do not match");

            assertEquals(1, statusCode, "Status codes do not match");

        }
    }

}
