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

class MiDataReportingTriggerTest {
    MiDataReportingTrigger miDataReportingTrigger;

    private static MockWebServer mockPublicationServicesService;

    @BeforeAll
    static void setUp() throws IOException {
        mockPublicationServicesService = new MockWebServer();
        mockPublicationServicesService.start(4552);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockPublicationServicesService.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String url = "http://localhost:4552";
        miDataReportingTrigger = new MiDataReportingTrigger(webClient, url);
    }

    @Test
    void testTrigger() throws InterruptedException {
        mockPublicationServicesService.enqueue(new MockResponse());
        miDataReportingTrigger.trigger();

        RecordedRequest recordedRequest = mockPublicationServicesService.takeRequest();
        assertEquals("POST", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/notify/mi/report", recordedRequest.getPath(), "Path not as expected");
    }

    @Test
    void testIsApplicable() {
        assertTrue(miDataReportingTrigger.isApplicable(ScheduleTypes.MI_DATA_REPORTING),
                   "Marked as not applicable when applicable enum");
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(miDataReportingTrigger.isApplicable(ScheduleTypes.MEDIA_APPLICATION_REPORTING),
                    "Marked as applicable when not applicable enum");
    }
}
