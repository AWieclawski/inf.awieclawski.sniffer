package inf.awieclawski.sniffer.schdlrs;

import inf.awieclawski.sniffer.cnfgs.ThreadPoolTaskSchedulerConfig;
import inf.awieclawski.sniffer.tsks.TasksExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn(TasksExecutor.BEAN_NAME)
public class TaskSchedulerImpl {

    private final TasksExecutor tasksExecutor;

    private final ThreadPoolTaskScheduler taskScheduler;

    @Qualifier(ThreadPoolTaskSchedulerConfig.CRON_TRIGGER)
    private final TriggerHolder triggerHolder;

    private final Map<String, ScheduledFuture<?>> scheduledTasksMap =
            new IdentityHashMap<>();

    @PostConstruct
    public void scheduleRunnableWithTrigger() {
        CronTrigger trigger = triggerHolder.getTrigger();
        addTask(new RunnableTask("Crone Trigger", trigger.getExpression()), trigger);
    }

    public List<String> replaceCronInTask(String cronExpression) {
        triggerHolder.setCroneTrigger(cronExpression);
        final String[] foundKeyWrapper = new String[1];
        scheduledTasksMap.entrySet().stream().findFirst().ifPresentOrElse(
                it -> {
                    foundKeyWrapper[0] = it.getKey();
                    if (!Objects.equals(cronExpression, foundKeyWrapper[0])) {
                        cancelScheduledTask(foundKeyWrapper[0]);
                    }
                },
                () -> addTask(new RunnableTask("Crone Trigger", cronExpression), triggerHolder.getTrigger())
        );
        if (foundKeyWrapper[0] != null && !Objects.equals(cronExpression, foundKeyWrapper[0])) {
            scheduledTasksMap.remove(foundKeyWrapper[0]);
            addTask(new RunnableTask("Crone Trigger", cronExpression), triggerHolder.getTrigger());
        }
        return getScheduledCrons();
    }

    public List<String> getScheduledCrons() {
        return new ArrayList<>(scheduledTasksMap.keySet());
    }

    private void addTask(RunnableTask task, Trigger trigger) {
        ScheduledFuture<?> future = taskScheduler.schedule(task, trigger);
        scheduledTasksMap.put(task.id, future);
        log.info("Task with cron [{}] added - OK", task.id);
    }

    private void cancelScheduledTask(String id) {
        ScheduledFuture<?> future = scheduledTasksMap.get(id);
        if (null != future) {
            future.cancel(true);
            log.warn("Task with cron [{}] canceled - OK", id);
        }
    }

    class RunnableTask implements Runnable {

        private String id;

        private String message;

        public RunnableTask(String message, String id) {
            this.message = message;
            this.id = id;
        }

        @Override
        public void run() {
            log.info(">>> {} run start time: [{}]", message, Instant.now().toString());
            tasksExecutor.doJobs();
            log.info("End time: {} <<<", Instant.now().toString());
        }
    }

}
