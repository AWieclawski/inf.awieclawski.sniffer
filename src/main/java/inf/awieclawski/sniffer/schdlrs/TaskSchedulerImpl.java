package inf.awieclawski.sniffer.schdlrs;

import inf.awieclawski.sniffer.cnfgs.ThreadPoolTaskSchedulerConfig;
import inf.awieclawski.sniffer.dtos.TaskDto;
import inf.awieclawski.sniffer.rpstr.DataRepository;
import inf.awieclawski.sniffer.tsks.TasksExecutor;
import inf.awieclawski.sniffer.utls.CronCheck;
import inf.awieclawski.sniffer.utls.ReflectionUtils;
import inf.awieclawski.sniffer.xcptns.TaskCallException;
import inf.awieclawski.sniffer.xcptns.TaskSchedulerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn(TasksExecutor.BEAN_NAME)
public class TaskSchedulerImpl {

    private final DataRepository dataRepository;
    private final TasksExecutor tasksExecutor;
    private final ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    @Qualifier(ThreadPoolTaskSchedulerConfig.CRON_TRIGGER)
    private TriggerHolder triggerHolder;

    private final Map<String, ScheduledFuture<?>> scheduledTasksMap =
            new IdentityHashMap<>();

    @PostConstruct
    public void initScheduledTasks() {
        CronTrigger standardTrigger = triggerHolder.getStandardTrigger();
        dataRepository.getDtos().forEach(dto -> {
            CronTrigger trigger = dto.getCronExpression() != null ? new CronTrigger(dto.getCronExpression()) : standardTrigger;
            CronCheck.checkCron(dto.getCronExpression());
            if (Boolean.TRUE.equals(dto.getSniffActive())) {
                String message = String.format("Task [%s|%s|%s] ", dto.getSniffedAddress(), dto.getPathVariables(), dto.getCronExpression());
                addTask(new RunnableTask(message, dto.getUniqueName(), dto).withRunnableMethod(() -> tasksExecutor.multiExecute(dto)), trigger);
            }
        });
    }

    public List<String> replaceStandardCron(String cronExpression) {
        triggerHolder.setStandardTrigger(cronExpression);
        return List.of(cronExpression);
    }

    public List<TaskDto> replaceScheduledTasks() {
        scheduledTasksMap.keySet().forEach(this::cancelScheduledTask);
        scheduledTasksMap.clear();
        initScheduledTasks();
        return scheduledTasksMap.values().stream()
                .map(it -> getRunnableTask((DelegatingErrorHandlingRunnable) it))
                .map(RunnableTask::getDto).collect(Collectors.toList());
    }

    private RunnableTask getRunnableTask(DelegatingErrorHandlingRunnable der) {
        try {
            return (RunnableTask) ReflectionUtils.getFieldValue(der, "delegate");
        } catch (Exception e) {
            log.error("Runnable delegate [{}] can not be get! {}", der.toString(), e.getMessage());
        }
        throw new TaskSchedulerException("Runnable delegate error!");
    }

    public void addTaskToSchedulerJobs(TaskDto dto) {
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
        log.info("Added Task with cron [{}] - OK", task.id);
    }

    private void cancelScheduledTask(String id) {
        ScheduledFuture<?> future = scheduledTasksMap.get(id);
        if (null != future) {
            future.cancel(true);
            log.warn("Canceled Task with cron [{}] - OK", id);
        }
    }

    /**
     * vide: https://www.baeldung.com/java-runnable-callable
     */
    class RunnableTask implements Runnable {

        private String id;

        private String message;

        private Runnable runnableMethod;

        @Getter
        private TaskDto dto;

        public RunnableTask(String message, String id, TaskDto dto) {
            this.message = message;
            this.id = id;
            this.dto = dto;
        }

        public RunnableTask withRunnableMethod(Runnable runnableMethod) {
            this.runnableMethod = runnableMethod;
            return this;
        }

        @Override
        public void run() {
            log.info(">>> {} run start time: [{}]", message, Instant.now().toString());
            try {
                runnableMethod.run();
            } catch (Exception e) {
                throw new TaskCallException(e);
            }
            log.info("End time: {} <<<", Instant.now().toString());
        }

    }

}
