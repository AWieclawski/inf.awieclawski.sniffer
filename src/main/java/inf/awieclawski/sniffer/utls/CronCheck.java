package inf.awieclawski.sniffer.utls;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.support.CronExpression;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CronCheck {

    public static void checkCron(String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new RuntimeException("Cron expression not valid! [" + cronExpression + "]");
        }
    }
}
