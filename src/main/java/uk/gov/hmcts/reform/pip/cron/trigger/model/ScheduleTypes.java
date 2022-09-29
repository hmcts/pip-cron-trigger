package uk.gov.hmcts.reform.pip.cron.trigger.model;

import uk.gov.hmcts.reform.pip.cron.trigger.triggers.ExpiredArtefactsTrigger;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.MediaApplicationReportingTrigger;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.NoMatchArtefactsTrigger;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.SubscriptionsTrigger;
import uk.gov.hmcts.reform.pip.cron.trigger.triggers.Trigger;

/**
 * This class contains the expected types of schedules that could be run
 */
public enum ScheduleTypes {
    MEDIA_APPLICATION_REPORTING(MediaApplicationReportingTrigger.class),
    ACCOUNT_VERIFICATION_CHECK(ExpiredArtefactsTrigger.class),
    SUBSCRIPTIONS(SubscriptionsTrigger.class),
    NO_MATCH_ARTEFACTS(NoMatchArtefactsTrigger.class),
    EXPIRED_ARTEFACTS(ExpiredArtefactsTrigger.class);

    private Class<? extends Trigger> triggerClass;

    ScheduleTypes(Class<? extends Trigger> triggerClass) {
        this.triggerClass = triggerClass;
    }

    public Class<? extends Trigger> getTriggerClass() {
        return triggerClass;
    }
}