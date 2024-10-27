package inf.awieclawski.sniffer.rpstr;

import inf.awieclawski.sniffer.dtos.TasksDto;
import inf.awieclawski.sniffer.prps.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component(DataRepository.BEAN_NAME)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@DependsOn(AppProperties.BEAN_NAME)
@RequiredArgsConstructor
@Slf4j
public class DataRepository {

    public static final String BEAN_NAME = "inf.awieclawski.sniffer.rpstr.DataRepository";

    private final AppProperties appProperties;

    private List<TasksDto> tasksDtos = new ArrayList<>();

    @PostConstruct
    public void hello() {
        log.info("Requests data list initiated size: [{}] - OK!", getDtos().size());
    }

    public List<TasksDto> getDtos() {
        if (CollectionUtils.isEmpty(this.tasksDtos)) {
            this.tasksDtos = Collections.synchronizedList(new ArrayList<>(appProperties.getDtoList()));
        }
        return this.tasksDtos;
    }

    public void setDtos(List<TasksDto> dtoList) {
        if (this.tasksDtos != null) {
            this.tasksDtos.clear();
        }
        this.tasksDtos = Collections.synchronizedList(new ArrayList<>(dtoList));
        log.info("Requests data list after update size: [{}] - OK!", getDtos().size());
    }

    public void addDto(TasksDto dto) {
        getDtos().add(dto);
    }

}
