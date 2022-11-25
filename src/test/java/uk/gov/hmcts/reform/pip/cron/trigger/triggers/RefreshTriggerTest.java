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

class RefreshTriggerTest {

    RefreshTrigger refreshTrigger;

    private static MockWebServer mockDataManagementService;

    private static MockWebServer mockSubscriptionManagementService;

    private static MockWebServer mockAccountManagementService;

    @BeforeAll
    static void setUp() throws IOException {
        mockDataManagementService = new MockWebServer();
        mockDataManagementService.start(4552);

        mockSubscriptionManagementService = new MockWebServer();
        mockSubscriptionManagementService.start(4553);

        mockAccountManagementService = new MockWebServer();
        mockAccountManagementService.start(4554);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockDataManagementService.shutdown();
        mockSubscriptionManagementService.shutdown();
        mockAccountManagementService.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();

        String dataManagementUrl = "http://localhost:4552";
        String subscriptionManagementUrl = "http://localhost:4553";
        String accountManagementUrl = "http://localhost:4554";

        refreshTrigger =
            new RefreshTrigger(webClient, dataManagementUrl, subscriptionManagementUrl, accountManagementUrl);
    }

    @Test
    void testTrigger() throws InterruptedException {
        mockDataManagementService.enqueue(new MockResponse());
        mockSubscriptionManagementService.enqueue(new MockResponse());
        mockAccountManagementService.enqueue(new MockResponse());

        refreshTrigger.trigger();

        RecordedRequest recordedRequest = mockDataManagementService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/view/refresh", recordedRequest.getPath(), "Path not as expected");

        recordedRequest = mockSubscriptionManagementService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/view/refresh", recordedRequest.getPath(), "Path not as expected");

        recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/view/refresh", recordedRequest.getPath(), "Path not as expected");
    }

    @Test
    void testIsApplicable() {
        assertTrue(refreshTrigger.isApplicable(ScheduleTypes.REFRESH_VIEWS),
                   "Marked as not applicable when applicable enum");
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(refreshTrigger.isApplicable(ScheduleTypes.MEDIA_APPLICATION_REPORTING),
                    "Marked as applicable when not applicable enum");
    }

}
