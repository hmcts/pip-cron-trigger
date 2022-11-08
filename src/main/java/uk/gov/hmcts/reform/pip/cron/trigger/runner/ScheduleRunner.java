package uk.gov.hmcts.reform.pip.cron.trigger.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.pip.cron.trigger.config.CronTimerProperties;
import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.Trigger;

import java.util.List;
import java.util.Optional;

/**
 * This is the runner that is started when the job begins.
 * It selects the correct trigger based on the input, and then exits
 */
@Service
@Slf4j
@SuppressWarnings("PMD.DoNotTerminateVM")
public class ScheduleRunner implements CommandLineRunner {

    @Autowired
    private List<? extends Trigger> triggers;

    @Autowired
    private CronTimerProperties cronTimerProperties;

    @Override
    public void run(String... args) {
        if (!EnumUtils.isValidEnum(ScheduleTypes.class, cronTimerProperties.getTriggerType())) {
            log.error("Invalid or no schedule type set. Exiting");
            System.exit(1);
        }

        ScheduleTypes triggerType = ScheduleTypes.valueOf(cronTimerProperties.getTriggerType());

        Optional<? extends Trigger> foundTrigger =
            triggers.stream().filter(trigger -> trigger.isApplicable(triggerType)).findFirst();

        if (foundTrigger.isPresent()) {
            foundTrigger.get().trigger();
        } else {
            log.error("Failed to find trigger. Exiting");
            System.exit(1);
        }
    }

}
