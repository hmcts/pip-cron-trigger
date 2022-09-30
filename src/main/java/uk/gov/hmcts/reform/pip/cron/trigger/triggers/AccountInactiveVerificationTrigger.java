package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class AccountInactiveVerificationTrigger implements Trigger {

    @Autowired
    WebClient webClient;

    @Value("${service-to-service.account-management}")
    private String url;

    private final List<String> notifyUrls = List.of(
        "/media/inactive/notify",
        "/admin/inactive/notify",
        "/idam/inactive/notify"
    );

    private final List<String> deleteUrls = List.of(
        "/media/inactive",
        "/admin/inactive",
        "/idam/inactive"
    );

    @Override
    public void trigger() {
        notifyUrls.forEach(notifyUrl ->
            webClient.post().uri(String.format("%s/account/%s", url, notifyUrl))
                .attributes(clientRegistrationId("accountManagementApi"))
                .retrieve()
                .bodyToMono(String.class).block());

        deleteUrls.forEach(deleteUrl ->
            webClient.delete().uri(String.format("%s/account/%s", url, deleteUrl))
                .attributes(clientRegistrationId("accountManagementApi"))
                .retrieve()
                .bodyToMono(String.class).block());

    }
}
