package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchivedAccountDeletionTriggerTest {
    ArchivedAccountDeletionTrigger archivedAccountDeletionTrigger;

    private static MockWebServer mockAccountManagementService;

    @BeforeAll
    static void setUp() throws IOException {
        mockAccountManagementService = new MockWebServer();
        mockAccountManagementService.start(4551);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockAccountManagementService.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String url = "http://localhost:4551";
        archivedAccountDeletionTrigger = new ArchivedAccountDeletionTrigger(webClient, url);
    }

    @Test
    void testTrigger() throws InterruptedException {
        mockAccountManagementService.enqueue(new MockResponse());
        archivedAccountDeletionTrigger.trigger();

        RecordedRequest recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/account/archived", recordedRequest.getPath(),
                     "Path not as expected");

    }

    @Test
    void testIsApplicable() {
        assertTrue(
            archivedAccountDeletionTrigger.isApplicable(ScheduleTypes.DELETE_ARCHIVED_ACCOUNTS),
            "Marked as not applicable when applicable enum");
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(
            archivedAccountDeletionTrigger.isApplicable(ScheduleTypes.ACCOUNT_INACTIVE_VERIFICATION),
            "Marked as applicable when not applicable enum");
    }
}
