package com.github.afanas10101111.mp.filter;

import com.github.afanas10101111.mp.config.ProxyConfig;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.RequestBodyChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private ProxyConfig config;

    @Mock
    private RequestBodyChecker checker;

    @InjectMocks
    private PreFilter preFilter;

    @Test
    void requestWithEmptyBodyShouldNotBeStubbed() {
        setupMocks(null);
        Mono<Void> filter = preFilter.filter(exchange, chain);

        Mockito.verify(checker, Mockito.never()).getMockRule(anyString());
        Mockito.verify(exchange, Mockito.never()).getResponse();
        Mockito.verify(exchange, Mockito.never()).getAttributes();
        assertThat(filter).isEqualTo(empty);
    }

    @Test
    void requestNotMatchingRulesShouldNotBeStubbed() {
        setupMocks("someBodyWithoutStub", null);
        Mono<Void> filter = preFilter.filter(exchange, chain);

        Mockito.verify(checker, Mockito.only()).getMockRule(anyString());
        Mockito.verify(exchange, Mockito.never()).getResponse();
        Mockito.verify(exchange, Mockito.never()).getAttributes();
        assertThat(filter).isEqualTo(empty);
    }

    @Test
    void requestMatchingRulesShouldBeStubbed() {
        setupMocks("someBodyWithStub", "someStub");
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        Mockito.when(exchange.getResponse()).thenReturn(response);
        Mono<Void> filter = preFilter.filter(exchange, chain);

        Mockito.verify(checker, Mockito.only()).getMockRule(anyString());
        Mockito.verify(exchange, Mockito.atLeastOnce()).getResponse();
        Mockito.verify(exchange, Mockito.atLeastOnce()).getAttributes();
        assertThat(filter).isNotEqualTo(empty);
    }

    private void setupMocks(String exchangeStubbedResponse, String checkerStubbedResponse) {
        setupMocks(exchangeStubbedResponse);
        MockRule rule = null;
        if (checkerStubbedResponse != null) {
            rule = new MockRule();
            rule.setBody(checkerStubbedResponse);
        }
        Mockito.when(checker.getMockRule(anyString())).thenReturn(Optional.ofNullable(rule));
    }

    private void setupMocks(String exchangeStubbedResponse) {
        Mockito.when(chain.filter(exchange)).thenReturn(empty);
        Mockito.when(exchange.getAttribute(anyString())).thenReturn(exchangeStubbedResponse);
    }
}
