package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

/**
 * Trigger to Refresh the Views.
 */
@Service
public class RefreshTrigger implements Trigger {

    private final WebClient webClient;

    private final String dataManagementUrl;
    private final String subscriptionManagementUrl;
    private final String accountManagementUrl;

    public RefreshTrigger(@Autowired WebClient webClient,
                          @Value("${service-to-service.data-management}") String dataManagementUrl,
                          @Value("${service-to-service.subscription-management}") String subscriptionManagementUrl,
                          @Value("${service-to-service.account-management}") String accountManagementUrl) {
        this.webClient = webClient;
        this.dataManagementUrl = dataManagementUrl;
        this.subscriptionManagementUrl = subscriptionManagementUrl;
        this.accountManagementUrl = accountManagementUrl;
    }


    @Override
    public void trigger() {
        webClient.post().uri(String.format("%s/view/refresh", dataManagementUrl))
            .attributes(clientRegistrationId("dataManagementApi"))
            .retrieve()
            .bodyToMono(String.class).block();

        webClient.post().uri(String.format("%s/view/refresh", subscriptionManagementUrl))
            .attributes(clientRegistrationId("subscriptionManagementApi"))
            .retrieve()
            .bodyToMono(String.class).block();

        webClient.post().uri(String.format("%s/view/refresh", accountManagementUrl))
            .attributes(clientRegistrationId("accountManagementApi"))
            .retrieve()
            .bodyToMono(String.class).block();
    }

    @Override
    public boolean isApplicable(ScheduleTypes scheduleTypes) {
        return scheduleTypes.equals(ScheduleTypes.REFRESH_VIEWS);
    }
}
