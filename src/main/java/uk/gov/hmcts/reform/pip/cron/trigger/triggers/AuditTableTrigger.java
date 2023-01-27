package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

/**
 * Trigger that deletes old rows in the Audit Table.
 */
@Service
public class AuditTableTrigger implements Trigger {

    WebClient webClient;

    private final String url;

    public AuditTableTrigger(@Autowired WebClient webClient,
                                              @Value("${service-to-service.account-management}") String url) {
        this.webClient = webClient;
        this.url = url;
    }


    @Override
    public void trigger() {
        webClient.delete().uri(String.format("%s/audit", url))
            .attributes(clientRegistrationId("accountManagementApi"))
            .retrieve()
            .bodyToMono(String.class).block();
    }

    @Override
    public boolean isApplicable(ScheduleTypes scheduleTypes) {
        return scheduleTypes.equals(ScheduleTypes.CLEAR_AUDIT_TABLE);
    }

}
