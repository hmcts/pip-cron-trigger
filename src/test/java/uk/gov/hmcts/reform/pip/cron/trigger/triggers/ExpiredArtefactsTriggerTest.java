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

public class ExpiredArtefactsTriggerTest {

    ExpiredArtefactsTrigger expiredArtefactsTrigger;

    private static MockWebServer mockDataManagementService;

    @BeforeAll
    static void setUp() throws IOException {
        mockDataManagementService = new MockWebServer();
        mockDataManagementService.start(4551);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockDataManagementService.shutdown();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String url = "http://localhost:4551";
        expiredArtefactsTrigger = new ExpiredArtefactsTrigger(webClient, url);
    }


    @Test
    void testTrigger() throws InterruptedException {
        mockDataManagementService.enqueue(new MockResponse());
        expiredArtefactsTrigger.trigger();

        RecordedRequest recordedRequest = mockDataManagementService.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod(), "Method not as expected");
        assertEquals("/publication/expired", recordedRequest.getPath(), "Path not as expected");
    }

    @Test
    void testIsApplicable() {
        assertTrue(expiredArtefactsTrigger.isApplicable(ScheduleTypes.EXPIRED_ARTEFACTS),
                   "Marked as not applicable when applicable enum");
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(expiredArtefactsTrigger.isApplicable(ScheduleTypes.NO_MATCH_ARTEFACTS),
                   "Marked as applicable when not applicable enum");
    }

}
