package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;

public class SubscriptionsTriggerTest {

    @Mock
    WebClient webClient;

    @InjectMocks
    SubscriptionsTrigger subscriptionsTrigger;

    @Test
    void testTrigger() {

    }

    @Test
    void testIsApplicable() {

    }

}
