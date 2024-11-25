package inf.awieclawski.sniffer.utls;

import inf.awieclawski.sniffer.clnts.ClientImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlCheck {

    public static boolean askForHeaders(String urlName, boolean wiretapEnabled) {
        boolean result = false;
        try {
            ClientImpl clientBase = ClientImpl.getClient(urlName, null, wiretapEnabled);
            HttpHeaders aHeaders = clientBase.getHeaders().block();
            if (aHeaders != null) {
                log.debug(" >> Url [{}] - OK", urlName);
                recogniseConnection(aHeaders);
                result = true;
            }
            return result;
        } catch (Exception e) {
            log.error("Url check error {}! <<", e.getMessage(), e.getCause());
            return result;
        }
    }

    public static boolean askForHeaders(String urlBase, String token, List<String> paths, boolean wiretapEnabled) {
        boolean result = false;
        try {
            ClientImpl clientBase = ClientImpl.getClient(urlBase, token, wiretapEnabled);
            List<HttpHeaders> aHeadersList = clientBase.getHeadersBySuccessfulEndPaths(paths).collectList().block();
            if (aHeadersList != null) {
                log.debug(" >> Url [{}] - OK", urlBase);
                aHeadersList.stream().filter(hd -> !hd.isEmpty())
                        .forEach(UrlCheck::recogniseConnection);
//                        .forEach(UrlCheck::recogniseRedirection);
                result = true;
            }
            return result;
        } catch (Exception e) {
            log.error("Url check error {}! <<", e.getMessage(), e.getCause());
            return result;
        }
    }

    private static void recogniseConnection(HttpHeaders aHeaders) {
        aHeaders.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(e -> log.debug(" - k: [{}] | v: {}", e.getKey(), e.getValue()));
    }

    private static void recogniseRedirection(HttpHeaders aHeaders) {
        aHeaders.entrySet().stream()
                .filter(Objects::nonNull)
                .filter(ets -> "Location".equals(ets.getKey()))
                .findFirst().ifPresent(ents -> ents.getValue()
                        .forEach(v -> log.debug("[{}|{}]", ents.getKey(), v)));
    }
}
