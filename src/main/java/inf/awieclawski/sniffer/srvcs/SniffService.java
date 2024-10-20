package inf.awieclawski.sniffer.srvcs;

import inf.awieclawski.sniffer.rpstr.DataRepository;
import inf.awieclawski.sniffer.utls.UrlCheck;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service(SniffService.BEAN_NAME)
@DependsOn(DataRepository.BEAN_NAME)
public class SniffService {

    public static final String BEAN_NAME = "inf.awieclawski.sniffer.srvcs.SniffService";

    public void doSniff(String url) {
        UrlCheck.httpExists(url);
    }

}
