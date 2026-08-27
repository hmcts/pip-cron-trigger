package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class ArchivedAccountDeletionTrigger implements Trigger {
    WebClient webClient;

    private final String url;

    public ArchivedAccountDeletionTrigger(@Autowired WebClient webClient,
                                          @Value("${service-to-service.account-management}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    @Override
    public void trigger() {
        webClient.delete().uri(String.format("%s/account/archived", url))
            .attributes(clientRegistrationId("accountManagementApi"))
            .retrieve()
            .bodyToMono(String.class).block();
    }

    @Override
    public boolean isApplicable(ScheduleTypes scheduleTypes) {
        return scheduleTypes.equals(ScheduleTypes.DELETE_ARCHIVED_ACCOUNTS);
    }
}
