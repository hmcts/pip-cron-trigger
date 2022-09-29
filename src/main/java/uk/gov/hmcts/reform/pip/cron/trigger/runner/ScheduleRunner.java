package uk.gov.hmcts.reform.pip.cron.trigger.runner;

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
public class ScheduleRunner implements CommandLineRunner {

    @Autowired
    private List<? extends Trigger> triggers;

    @Override
    public void run(String... args) {
        ScheduleTypes triggerType = ScheduleTypes.SUBSCRIPTIONS;

        Optional<? extends Trigger> foundTrigger =
            triggers.stream().filter(trigger -> triggerType.getTriggerClass().equals(trigger.getClass())).findFirst();

        if (foundTrigger.isPresent()) {
            foundTrigger.get().trigger();
        } else {
            System.out.println("Failed to find trigger. Exiting");
        }
    }

}
