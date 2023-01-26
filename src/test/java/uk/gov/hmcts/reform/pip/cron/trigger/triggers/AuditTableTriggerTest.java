package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditTableTriggerTest {

    AuditTableTrigger auditTableTrigger;

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
        auditTableTrigger = new AuditTableTrigger(webClient, url);
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
