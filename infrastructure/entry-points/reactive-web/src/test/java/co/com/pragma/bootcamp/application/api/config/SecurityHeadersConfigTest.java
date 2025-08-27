package co.com.pragma.bootcamp.application.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecurityHeadersConfigTest {

    private SecurityHeadersConfig securityHeadersConfig;
    private WebFilterChain chain;

    @BeforeEach
    void setUp() {
        securityHeadersConfig = new SecurityHeadersConfig();
        chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void filter_shouldAddSecurityHeaders() {
        // given
        MockServerWebExchange exchange = mock(MockServerWebExchange.class);
        when(exchange.getResponse()).thenReturn(new MockServerHttpResponse());
        MockServerHttpResponse response = exchange.getResponse();

        // when
        Mono<Void> result = securityHeadersConfig.filter(exchange, chain);

        // then
        StepVerifier.create(result).verifyComplete();

        HttpHeaders headers = response.getHeaders();

        assertThat(headers.getFirst("Content-Security-Policy"))
                .isEqualTo("default-src 'self'; frame-ancestors 'self'; form-action 'self'");
        assertThat(headers.getFirst("Strict-Transport-Security")).isEqualTo("max-age=31536000;");
        assertThat(headers.getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(headers.getFirst("Server")).isEqualTo("");
        assertThat(headers.getFirst("Cache-Control")).isEqualTo("no-store");
        assertThat(headers.getFirst("Pragma")).isEqualTo("no-cache");
        assertThat(headers.getFirst("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin");

        // Verificamos que la cadena continúe
        verify(chain, times(1)).filter(exchange);
    }
}
