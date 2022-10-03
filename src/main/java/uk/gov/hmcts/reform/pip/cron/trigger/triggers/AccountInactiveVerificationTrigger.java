package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class AccountInactiveVerificationTrigger implements Trigger {

    WebClient webClient;

    private final String url;

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

    public AccountInactiveVerificationTrigger(@Autowired WebClient webClient,
                                              @Value("${service-to-service.account-management}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

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

    @Override
    public boolean isApplicable(ScheduleTypes scheduleTypes) {
        return scheduleTypes.equals(ScheduleTypes.ACCOUNT_INACTIVE_VERIFICATION);
    }

}
