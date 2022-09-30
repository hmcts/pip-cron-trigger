package uk.gov.hmcts.reform.pip.cron.trigger.runner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ScheduleRunnerTest {

    @InjectMocks
    ScheduleRunner scheduleRunner;

    @Test
    void testRunnerWhereNoArgProvided() {

    }

    @Test
    void testRunnerWhereInvalidArgProvided() {

    }

    @Test
    void testRunnerWhereSuccessfullyTriggered() {

    }

    @Test
    void testRunnerWhereCouldNotFindTrigger() {


    }

}
