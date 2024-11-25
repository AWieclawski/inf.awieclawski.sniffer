package inf.awieclawski.sniffer.clnts;

import inf.awieclawski.sniffer.clnts.base.BaseWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Flux<HttpHeaders> getHeadersBySuccessfulEndPaths(List<String> endPaths) {
        return Flux.fromIterable(endPaths).filter(it -> !it.isEmpty())
                .flatMap(this::getHeadersByEndPath);
    }

    public Mono<HttpHeaders> getHeadersByEndPath(String endPath) {
        return this.webClient.head().uri("/{endPath}", endPath).exchangeToMono(response -> {
            if (response.statusCode().isError()) {
                return Mono.empty();
            } else {
                return response.toEntity(String.class).map(HttpEntity::getHeaders);
            }
        });
    }


}
