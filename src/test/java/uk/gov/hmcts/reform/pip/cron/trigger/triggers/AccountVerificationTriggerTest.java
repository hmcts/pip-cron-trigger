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
class AccountVerificationTriggerTest {

    AccountInactiveVerificationTrigger accountInactiveVerificationTrigger;

    private static MockWebServer mockAccountManagementService;

    private static final String POST_METHOD = "POST";
    private static final String DELETE_METHOD = "DELETE";
    private static final String METHOD_NOT_AS_EXPECTED = "Method not as expected";
    private static final String PATH_NOT_AS_EXPECTED = "Path not as expected";

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
        assertEquals(POST_METHOD, recordedRequest.getMethod(), METHOD_NOT_AS_EXPECTED);
        assertEquals("/account/media/inactive/notify", recordedRequest.getPath(), PATH_NOT_AS_EXPECTED);

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals(POST_METHOD, recordedRequest.getMethod(), METHOD_NOT_AS_EXPECTED);
        assertEquals("/account/admin/inactive/notify", recordedRequest.getPath(), PATH_NOT_AS_EXPECTED);

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals(POST_METHOD, recordedRequest.getMethod(), METHOD_NOT_AS_EXPECTED);
        assertEquals("/account/idam/inactive/notify", recordedRequest.getPath(), PATH_NOT_AS_EXPECTED);

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals(DELETE_METHOD, recordedRequest.getMethod(), METHOD_NOT_AS_EXPECTED);
        assertEquals("/account/media/inactive", recordedRequest.getPath(), PATH_NOT_AS_EXPECTED);

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals(DELETE_METHOD, recordedRequest.getMethod(), METHOD_NOT_AS_EXPECTED);
        assertEquals("/account/admin/inactive", recordedRequest.getPath(), PATH_NOT_AS_EXPECTED);

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals(DELETE_METHOD, recordedRequest.getMethod(), METHOD_NOT_AS_EXPECTED);
        assertEquals("/account/idam/inactive", recordedRequest.getPath(), PATH_NOT_AS_EXPECTED);
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
