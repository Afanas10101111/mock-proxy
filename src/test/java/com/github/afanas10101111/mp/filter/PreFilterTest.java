package com.github.afanas10101111.mp.filter;

import com.github.afanas10101111.mp.config.ProxyConfig;
import com.github.afanas10101111.mp.service.RequestBodyChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class PreFilterTest {
    private final Mono<Void> empty = Mono.empty();

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private ProxyConfig proxyConfig;

    @Mock
    private RequestBodyChecker checker;

    @Test
    void requestWithEmptyBodyShouldNotBeStubbed() {
        setupMocks(null);
        Mono<Void> filter = new PreFilter(proxyConfig, checker).filter(exchange, chain);

        Mockito.verify(checker, Mockito.never()).getStubbedResponse(anyString());
        Mockito.verify(exchange, Mockito.never()).getResponse();
        Mockito.verify(exchange, Mockito.never()).getAttributes();
        assertThat(filter).isEqualTo(empty);
    }

    @Test
    void requestNotMatchingRulesShouldNotBeStubbed() {
        setupMocks("someBodyWithoutStub", null);
        Mono<Void> filter = new PreFilter(proxyConfig, checker).filter(exchange, chain);

        Mockito.verify(checker, Mockito.only()).getStubbedResponse(anyString());
        Mockito.verify(exchange, Mockito.never()).getResponse();
        Mockito.verify(exchange, Mockito.never()).getAttributes();
        assertThat(filter).isEqualTo(empty);
    }

    @Test
    void requestMatchingRulesShouldBeStubbed() {
        setupMocks("someBodyWithStub", "someStub");
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        Mockito.when(exchange.getResponse()).thenReturn(response);
        Mono<Void> filter = new PreFilter(proxyConfig, checker).filter(exchange, chain);

        Mockito.verify(checker, Mockito.only()).getStubbedResponse(anyString());
        Mockito.verify(exchange, Mockito.atLeastOnce()).getResponse();
        Mockito.verify(exchange, Mockito.atLeastOnce()).getAttributes();
        assertThat(filter).isNotEqualTo(empty);
    }

    private void setupMocks(String exchangeStubbedResponse, String checkerStubbedResponse) {
        setupMocks(exchangeStubbedResponse);
        Mockito.when(checker.getStubbedResponse(anyString())).thenReturn(Optional.ofNullable(checkerStubbedResponse));
    }

    private void setupMocks(String exchangeStubbedResponse) {
        Mockito.when(chain.filter(exchange)).thenReturn(empty);
        Mockito.when(exchange.getAttribute(anyString())).thenReturn(exchangeStubbedResponse);
    }
}
