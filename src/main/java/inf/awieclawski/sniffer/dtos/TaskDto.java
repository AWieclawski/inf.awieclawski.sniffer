package inf.awieclawski.sniffer.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = {"sniffedAddress", "uniqueName"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private String sniffedAddress;

    private String requestMethod;

    private Boolean sniffActive;

    private List<String> pathVariables;

    private String cronExpression;

    private String uniqueName;

    private String keyToken;

}
