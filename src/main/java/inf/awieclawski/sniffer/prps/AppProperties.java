package inf.awieclawski.sniffer.prps;

import inf.awieclawski.sniffer.dtos.TaskDto;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Populate list of PropertiesDto from ex. application-dev.yml
 * Do not use it to manipulate Collections - do it in dedicated Repository
 */
@Component(AppProperties.BEAN_NAME)
@ConfigurationProperties(prefix = "application")
@Data
public class AppProperties {

    public static final String BEAN_NAME = "inf.awieclawski.sniffer.prps.AppProperties";

    private List<TaskDto> dtoList = new ArrayList<>();

}
