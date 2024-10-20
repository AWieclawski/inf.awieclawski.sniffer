package inf.awieclawski.sniffer.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "sniffedAddress")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TasksDto {

    private String sniffedAddress;

    private String requestMethod;

    private Boolean sniffActive;

    private List<String> pathVariables;

}
