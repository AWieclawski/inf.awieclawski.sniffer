package inf.awieclawski.sniffer.schdlrs;

import inf.awieclawski.sniffer.cnfgs.ThreadPoolTaskSchedulerConfig;
import inf.awieclawski.sniffer.dtos.TasksDto;
import inf.awieclawski.sniffer.prps.AppProperties;
import inf.awieclawski.sniffer.tsks.TasksExecutor;
import inf.awieclawski.sniffer.utls.CronCheck;
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
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn(TasksExecutor.BEAN_NAME)
public class TaskSchedulerImpl {

    private final AppProperties appProperties;
    private final TasksExecutor tasksExecutor;
    private final ThreadPoolTaskScheduler taskScheduler;

    @Qualifier(ThreadPoolTaskSchedulerConfig.CRON_TRIGGER)
    private final TriggerHolder triggerHolder;

    private final Map<String, ScheduledFuture<?>> scheduledTasksMap =
            new IdentityHashMap<>();

    @PostConstruct
    public void initScheduledTasks() {
        CronTrigger standardTrigger = triggerHolder.getStandardTrigger();
        appProperties.getDtoList().forEach(dto -> {
            CronTrigger trigger = dto.getCronExpression() != null ? new CronTrigger(dto.getCronExpression()) : standardTrigger;
            CronCheck.checkCron(dto.getCronExpression());
            String message = String.format("Task [%s|%s|%s] ", dto.getSniffedAddress(), dto.getPathVariables(), dto.getCronExpression());
            addTask(new RunnableTask(message, dto.getUniqueName(), dto), trigger);
        });
    }

    public List<String> replaceStandardCron(String cronExpression) {
        triggerHolder.setStandardTrigger(cronExpression);
        return List.of(cronExpression);
    }

    public void addTaskToSchedulerJobs(TasksDto dto) {
        CronTrigger standardTrigger = triggerHolder.getStandardTrigger();
        CronTrigger trigger = dto.getCronExpression() != null ? new CronTrigger(dto.getCronExpression()) : standardTrigger;
        CronCheck.checkCron(dto.getCronExpression());
        String message = String.format("Task [%s|%s|%s] ", dto.getSniffedAddress(), dto.getPathVariables(), dto.getCronExpression());
        addTask(new RunnableTask(message, dto.getUniqueName(), dto), trigger);
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

        private TasksDto dto;

        public RunnableTask(String message, String id, TasksDto dto) {
            this.message = message;
            this.id = id;
            this.dto = dto;
        }

        @Override
        public void run() {
            log.info(">>> {} run start time: [{}]", message, Instant.now().toString());
            tasksExecutor.execute(dto);
            log.info("End time: {} <<<", Instant.now().toString());
        }
    }

}
