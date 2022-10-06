package uk.gov.hmcts.reform.pip.cron.trigger.triggers;

import uk.gov.hmcts.reform.pip.cron.trigger.model.ScheduleTypes;

/**
 * Interface for the logic for each trigger.
 */
public interface Trigger {

    void trigger();

    boolean isApplicable(ScheduleTypes scheduleTypes);

}
