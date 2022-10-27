package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class MiDataReportingTrigger implements Trigger {
    WebClient webClient;

    private final String url;

    public MiDataReportingTrigger(@Autowired WebClient webClient,
                                  @Value("${service-to-service.publication-services}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    @Override
    public void trigger() {
        webClient.post().uri(String.format("%s/notify/mi/report", url))
            .attributes(clientRegistrationId("publicationServicesApi"))
            .retrieve()
            .bodyToMono(String.class).block();
    }

    @Override
    public boolean isApplicable(ScheduleTypes scheduleTypes) {
        return scheduleTypes.equals(ScheduleTypes.MI_DATA_REPORTING);
    }
}
