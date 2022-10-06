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

class MediaApplicationReportingTriggerTest {

    MediaApplicationReportingTrigger mediaApplicationReportingTrigger;

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
        mediaApplicationReportingTrigger = new MediaApplicationReportingTrigger(webClient, url);
    }

    @Test
    void testTrigger() throws InterruptedException {
        mockAccountManagementService.enqueue(new MockResponse());
        mediaApplicationReportingTrigger.trigger();

        RecordedRequest recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/application/reporting", recordedRequest.getPath(), "Path not as expected");
    }

    @Test
    void testIsApplicable() {
        assertTrue(mediaApplicationReportingTrigger.isApplicable(ScheduleTypes.MEDIA_APPLICATION_REPORTING),
                   "Marked as not applicable when applicable enum");
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(mediaApplicationReportingTrigger.isApplicable(ScheduleTypes.NO_MATCH_ARTEFACTS),
                    "Marked as applicable when not applicable enum");
    }

}
