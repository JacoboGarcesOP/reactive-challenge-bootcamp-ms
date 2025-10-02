package co.com.bancolombia.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigsTest {

    @Test
    void corsConfig_createsFilter() {
        CorsConfig config = new CorsConfig();
        CorsWebFilter filter = config.corsWebFilter("https://a.com,https://b.com");
        assertThat(filter).isNotNull();
    }

    @Test
    void securityHeaders_areApplied() {
        SecurityHeadersConfig sec = new SecurityHeadersConfig();

        MockServerWebExchange exchange = MockServerWebExchange.from(
                org.springframework.mock.http.server.reactive.MockServerHttpRequest
                        .get("/test").build());
        WebFilterChain chain = e -> Mono.empty();

        sec.filter(exchange, chain).block();

        HttpHeaders headers = exchange.getResponse().getHeaders();
        assertThat(headers.getFirst("Content-Security-Policy")).isNotBlank();
        assertThat(headers.getFirst("Strict-Transport-Security")).isNotBlank();
        assertThat(headers.getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(headers.getFirst("Server")).isEqualTo("");
        assertThat(headers.getFirst("Cache-Control")).isEqualTo("no-store");
        assertThat(headers.getFirst("Pragma")).isEqualTo("no-cache");
        assertThat(headers.getFirst("Referrer-Policy")).isNotBlank();
    }
}


