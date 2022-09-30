package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class SubscriptionsTrigger implements Trigger {

    @Autowired
    WebClient webClient;

    @Value("${service-to-service.data-management}")
    private String url;

    @Override
    public void trigger() {
        webClient.post().uri(String.format("%s/publication/latest/subscription", url))
            .attributes(clientRegistrationId("dataManagementApi"))
            .retrieve()
            .bodyToMono(String.class).block();
    }

}
