package inf.awieclawski.sniffer.cnfgs;

import inf.awieclawski.sniffer.schdlrs.TaskSchedulerImpl;
import inf.awieclawski.sniffer.schdlrs.TriggerHolder;
import inf.awieclawski.sniffer.utls.CronCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Slf4j
@Configuration
@EnableScheduling
@ComponentScan(basePackages = "inf.awieclawski.sniffer", basePackageClasses = {TaskSchedulerImpl.class})
public class ThreadPoolTaskSchedulerConfig {

    public static final String PREFIX = ThreadPoolTaskScheduler.class.getSimpleName();
    public static final String CRON_TRIGGER = "cronTriggerHolder";

    @Value("${cron.standard}")
    private String cronValue;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(PREFIX);
        return threadPoolTaskScheduler;
    }

    @Bean(CRON_TRIGGER)
    public TriggerHolder cronTrigger() {
        CronCheck.checkCron(cronValue);
        log.debug("Cron expression from properties: [{}] - OK!", cronValue);
        return new TriggerHolder(new CronTrigger(cronValue));
    }

}
