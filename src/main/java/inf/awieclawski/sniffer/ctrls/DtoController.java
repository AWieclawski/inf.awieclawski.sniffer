package inf.awieclawski.sniffer.ctrls;

import inf.awieclawski.sniffer.dtos.CronDto;
import inf.awieclawski.sniffer.dtos.TasksDto;
import inf.awieclawski.sniffer.rpstr.DataRepository;
import inf.awieclawski.sniffer.schdlrs.TaskSchedulerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping
@RestController
@RequiredArgsConstructor
public class DtoController {

    private final DataRepository dataRepository;
    private final TaskSchedulerImpl taskSchedulerBase;

    @GetMapping(path = "/dtos",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TasksDto>> dtoList() {
        final List<TasksDto> dtos = dataRepository.getDtos();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtos);
    }

    @PostMapping(path = "/dtos",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TasksDto>> newDtoList(@RequestBody List<TasksDto> dtoList) {
        checkObject(dtoList);
        final List<TasksDto> dtos = dataRepository.setDtos(dtoList);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dtos);
    }

    @GetMapping(path = "/cron",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getCron() {
        List<String> crons = taskSchedulerBase.getScheduledCrons();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(crons);
    }

    @PostMapping(path = "/cron",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> replaceCron(@RequestBody CronDto cronDto) {
        checkObject(cronDto);
        List<String> crons = taskSchedulerBase.replaceCronInTask(cronDto.getExpression());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(crons);
    }

    private void checkObject(Object object) {
        if (object == null) {
            throw new RuntimeException("No entity in the body!");
        }
    }

}
