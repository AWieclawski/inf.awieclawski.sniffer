package inf.awieclawski.sniffer.utls;

import inf.awieclawski.sniffer.clnts.ClientImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlCheck {

    public static boolean httpExists(String urlName) {
        boolean result = false;
        try {
            ClientImpl clientBase = ClientImpl.getClient(urlName, null, false);
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

    private static void recogniseConnection(HttpHeaders aHeaders) {
        aHeaders.entrySet().stream()
                .filter(Objects::nonNull)
                .forEach(e -> log.debug(" - k: [{}] | v: {}", e.getKey(), e.getValue()));
    }
}
