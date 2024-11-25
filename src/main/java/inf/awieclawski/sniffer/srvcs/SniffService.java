package inf.awieclawski.sniffer.srvcs;

import inf.awieclawski.sniffer.dtos.TaskDto;
import inf.awieclawski.sniffer.rpstr.DataRepository;
import inf.awieclawski.sniffer.utls.UrlCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service(SniffService.BEAN_NAME)
@DependsOn(DataRepository.BEAN_NAME)
public class SniffService {

    public static final String BEAN_NAME = "inf.awieclawski.sniffer.srvcs.SniffService";

    @Value("${base.wiretapEnabled}")
    private boolean wiretapEnabled;

    public void doSniff(String url) {
        UrlCheck.askForHeaders(url, wiretapEnabled);
    }

    public void doSniff(TaskDto dto) {
        UrlCheck.askForHeaders(dto.getSniffedAddress(), dto.getKeyToken(), dto.getPathVariables(), wiretapEnabled);
    }

}
