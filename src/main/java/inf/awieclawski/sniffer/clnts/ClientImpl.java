package inf.awieclawski.sniffer.clnts;

import inf.awieclawski.sniffer.clnts.base.BaseWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Slf4j
public class ClientImpl extends BaseWebClient {

    public ClientImpl(String url, String token, Boolean wiretapEnabled) {
        super(url, token, wiretapEnabled);
    }

    private ClientImpl() {
        super(null, null, null);
        new ClientImpl();
    }

    public static ClientImpl getClient(String url, String token, Boolean wiretapEnabled) {
        return new ClientImpl(url, token, wiretapEnabled);
    }

    public Mono<HttpHeaders> getHeaders() {
       return webClient.head().exchangeToMono(response -> {
            if (response.statusCode().isError()) {
                return response.createException().flatMap(Mono::error);
            } else {
                return response.toEntity(String.class).map(HttpEntity::getHeaders);
            }
        });
    }

}
