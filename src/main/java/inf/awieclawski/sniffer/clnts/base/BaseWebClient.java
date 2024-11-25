package inf.awieclawski.sniffer.clnts.base;


import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
public abstract class BaseWebClient {

    protected final WebClient webClient;

    // must be final to enable using in lambdas
    private final SslContext[] sslContextArray = new SslContext[1];

    protected BaseWebClient(String url, String token, Boolean wiretapEnabled) {
        ClientHttpConnector conn = new ReactorClientHttpConnector(getHttpClient(wiretapEnabled));
        webClient = WebClient.builder()
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(loggRequest());
                    exchangeFilterFunctions.add(loggResponse());
                })
                .clientConnector(conn)
                .baseUrl(url)
                .defaultHeaders(httpHeaders -> addAuthenticationHeaders(httpHeaders, token))
                .build();
    }

    protected BaseWebClient(String url, String token, Boolean wiretapEnabled, int timeout, int readTimeout, int writeTimeout, boolean secured) {
        webClient = WebClient.builder()
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(loggRequest());
                    exchangeFilterFunctions.add(loggResponse());
                })
                .clientConnector(clientConnector(timeout, readTimeout, writeTimeout, secured))
                .baseUrl(url)
                .defaultHeaders(httpHeaders -> addAuthenticationHeaders(httpHeaders, token))
                .build();
    }

    protected void addAuthenticationHeaders(HttpHeaders httpHeaders, String token) {
        if (token != null && !token.isEmpty()) {
            log.debug("Created WebClient authentication filter with token");
//            httpHeaders.set("Authorization", "token " + token); // used in git hub
            httpHeaders.set("Authorization", "Bearer " + token);
        } else {
            log.debug("WebClient without authentication headers");
        }
    }

    protected HttpClient getHttpClient(boolean wiretapEnabled) {
        HttpClient httpClient = HttpClient.create();
        if (Boolean.TRUE.equals(wiretapEnabled)) {
            httpClient = httpClient.wiretap(
                    this.getClass().getCanonicalName(), LogLevel.TRACE, AdvancedByteBufFormat.TEXTUAL);
        }
        return httpClient;
    }

    public ClientHttpConnector clientConnector(int timeout, int readTimeout, int writeTimeout, boolean secured) {

        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .doOnConnected(conn -> conn.addHandlerFirst(new ReadTimeoutHandler(readTimeout, TimeUnit.SECONDS))
                        .addHandlerFirst(new WriteTimeoutHandler(writeTimeout)));

        if (secured) {
            sslContextArray[0] = getSslContext()[0];
            httpClient.secure(con -> con.sslContext(sslContextArray[0]));
        }

        return new ReactorClientHttpConnector(httpClient);
    }

    private SslContext[] getSslContext() {

        try {
            sslContextArray[0] = (SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build());
        } catch (SSLException e) {
            log.error("SSLException thrown: {}", e.getMessage());
        }

        return sslContextArray;
    }


    public ExchangeFilterFunction loggRequest() {

        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            HttpHeaders headers = null;

            if (clientRequest != null) {
                URI requestUrl = clientRequest.url();
                log.debug("Request method={}, target url={}", clientRequest.method(), requestUrl);
                headers = clientRequest.headers();
            }

            if (headers != null) {
                headers.forEach((name, values) -> values
                        .forEach(value -> log.debug("Request header name={}, value={}", name, value)));
            }

            return clientRequest != null ? Mono.just(clientRequest) : Mono.empty();
        });
    }

    public ExchangeFilterFunction loggResponse() {

        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {

            if (clientResponse != null) {
                log.debug("Response status Code={}", clientResponse.statusCode());
                log.debug("Response content-length={}", clientResponse.headers().header("content-length"));
            }

            return clientResponse != null ? Mono.just(clientResponse) : Mono.empty();
        });
    }

}
