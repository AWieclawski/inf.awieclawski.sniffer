package inf.awieclawski.sniffer.prps;

import inf.awieclawski.sniffer.dtos.TasksDto;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Populate list of PropertiesDto from ex. application.yml
 */
@Component(AppProperties.BEAN_NAME)
@ConfigurationProperties(prefix = "application")
@Data
public class AppProperties {

    public static final String BEAN_NAME = "inf.awieclawski.sniffer.prps.AppProperties";

    private List<TasksDto> dtoList = new ArrayList<>();

}
