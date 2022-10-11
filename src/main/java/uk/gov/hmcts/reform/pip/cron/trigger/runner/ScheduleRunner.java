package uk.gov.hmcts.reform.pip.cron.trigger.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
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

    @Override
    public void run(String... args) {
        if (args.length > 0) log.info(args[0]);

        if (args.length != 1 || !EnumUtils.isValidEnum(ScheduleTypes.class, args[0])) {
            log.error("Invalid or no argument passed in. Exiting");
            System.exit(1);
        }

        ScheduleTypes triggerType = ScheduleTypes.valueOf(args[0]);

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
