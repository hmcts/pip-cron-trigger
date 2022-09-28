package uk.gov.hmcts.reform.pip.cron.trigger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application implements CommandLineRunner {

    public static void main(final String[] args) {
        System.out.println("WE HAVE STARTED THE APP");
        SpringApplication.run(Application.class, args);
        System.out.println("WE HAVE FINISHED THE APP");
    }

    @Override
    public void run(String... args) {
        System.out.println("WE ARE RUNNING!!");
        System.out.println("WE ARE FINISHED");

    }
}
