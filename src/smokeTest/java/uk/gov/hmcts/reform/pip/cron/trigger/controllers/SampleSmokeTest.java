package uk.gov.hmcts.reform.pip.cron.trigger.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SampleSmokeTest {

    @Test
    void smokeTest() {
        assertEquals("Placeholder", "Placeholder", "Placeholder for smoke test");
    }
}
