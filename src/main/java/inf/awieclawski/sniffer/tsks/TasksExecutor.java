package inf.awieclawski.sniffer.tsks;

import inf.awieclawski.sniffer.dtos.TasksDto;
import inf.awieclawski.sniffer.srvcs.SniffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Slf4j
@Component(TasksExecutor.BEAN_NAME)
@DependsOn(SniffService.BEAN_NAME)
@RequiredArgsConstructor
public class TasksExecutor {

    public static final String BEAN_NAME = "inf.awieclawski.sniffer.tsks.ScheduledTasks";

    private final SniffService sniffService;

    public void execute(TasksDto dto) {
        if (dto.getSniffActive() != null && dto.getSniffActive()) {
            try {
                sniffService.doSniff(dto.getSniffedAddress());
                if (dto.getPathVariables() != null) {
                    dto.getPathVariables().forEach(variable ->
                            sniffService.doSniff(dto.getSniffedAddress() + "/" + variable)
                    );
                }
            } catch (Exception e) {
                log.error("Sniffer Error for address: [{}]! {} ", dto.getSniffedAddress(), e.getMessage(), e.getCause());
            }
        }
    }

}
