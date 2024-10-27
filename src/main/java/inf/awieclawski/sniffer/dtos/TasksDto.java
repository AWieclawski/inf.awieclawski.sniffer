package inf.awieclawski.sniffer.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = {"sniffedAddress", "uniqueName"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TasksDto {

    private String sniffedAddress;

    private String requestMethod;

    private Boolean sniffActive;

    private List<String> pathVariables;

    private String cronExpression;

    private String uniqueName;

}
