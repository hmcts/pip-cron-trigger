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

class AuditTableTriggerTest {

    AuditTableTrigger auditTableTrigger;

    private static MockWebServer mockAccountManagementService;

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
        auditTableTrigger = new AuditTableTrigger(webClient, url);
    }

    @Test
    void testTrigger() throws InterruptedException {
        mockAccountManagementService.enqueue(new MockResponse());
        auditTableTrigger.trigger();

        RecordedRequest recordedRequest = mockAccountManagementService.takeRequest();
        assertEquals(DELETE_METHOD, recordedRequest.getMethod(), METHOD_NOT_AS_EXPECTED);
        assertEquals("/audit", recordedRequest.getPath(), PATH_NOT_AS_EXPECTED);
    }

    @Test
    void testIsApplicable() {
        assertTrue(auditTableTrigger.isApplicable(ScheduleTypes.CLEAR_AUDIT_TABLE),
                   "Marked as not applicable when applicable enum");
    }

    @Test
    void testIsNotApplicable() {
        assertFalse(auditTableTrigger.isApplicable(ScheduleTypes.NO_MATCH_ARTEFACTS),
                    "Marked as applicable when not applicable enum");
    }

}
