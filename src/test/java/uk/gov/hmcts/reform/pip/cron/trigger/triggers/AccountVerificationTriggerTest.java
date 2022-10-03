package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AccountVerificationTriggerTest {

    AccountInactiveVerificationTrigger accountInactiveVerificationTrigger;

    private static MockWebServer mockAccountManagementService;

    @BeforeAll
    static void setUp() throws IOException {
        mockAccountManagementService = new MockWebServer();
        mockAccountManagementService.start(4550);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockAccountManagementService.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String url = "http://localhost:4550";
        accountInactiveVerificationTrigger = new AccountInactiveVerificationTrigger(webClient, url);
    }

    @Test
    void testTrigger() throws InterruptedException {
        mockAccountManagementService.enqueue(new MockResponse());
        mockAccountManagementService.enqueue(new MockResponse());
        mockAccountManagementService.enqueue(new MockResponse());
        mockAccountManagementService.enqueue(new MockResponse());
        mockAccountManagementService.enqueue(new MockResponse());
        mockAccountManagementService.enqueue(new MockResponse());
        accountInactiveVerificationTrigger.trigger();

        RecordedRequest recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/account/media/inactive/notify", recordedRequest.getPath(), "Path not as expected");

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/account/admin/inactive/notify", recordedRequest.getPath(), "Path not as expected");

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/account/idam/inactive/notify", recordedRequest.getPath(), "Path not as expected");

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/account/media/inactive", recordedRequest.getPath(), "Path not as expected");

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/account/admin/inactive", recordedRequest.getPath(), "Path not as expected");

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/account/idam/inactive", recordedRequest.getPath(), "Path not as expected");
    }

    @Test
    void testIsApplicable() {
        assertTrue(accountInactiveVerificationTrigger.isApplicable(ScheduleTypes.ACCOUNT_INACTIVE_VERIFICATION),
                   "Marked as not applicable when applicable enum");
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(accountInactiveVerificationTrigger.isApplicable(ScheduleTypes.NO_MATCH_ARTEFACTS),
                   "Marked as applicable when not applicable enum");
    }

}
