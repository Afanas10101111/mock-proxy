package com.github.afanas10101111.mp.filter;

import com.github.afanas10101111.mp.config.ProxyConfig;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.RequestBodyChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class PreFilterTest {
    private static final String REQUEST_BODY_OBJECT = "cachedRequestBodyObject";
    private static final String URL_TEMPLATE = "/";
    private static final Mono<Void> empty = Mono.empty();

    private ServerWebExchange exchange;

    @Mock
    private GatewayFilterChain chainMock;

    @Mock
    private ProxyConfig configMock;

    @Mock
    private RequestBodyChecker checkerMock;

    @InjectMocks
    private PreFilter preFilter;

    @BeforeEach
    void setupExchange() {
        exchange = MockServerWebExchange.from(MockServerHttpRequest.post(URL_TEMPLATE).build());
    }

    @Test
    void getRequestWithEmptyBodyShouldNotBeStubbed() {
        exchange = MockServerWebExchange.from(MockServerHttpRequest.get(URL_TEMPLATE).build());
        Mono<Void> filter = preFilter.filter(exchange, chainMock);

        Mockito.verify(checkerMock, Mockito.never()).getMockRule(anyString());
        assertThat(filter).isNull();
        verifyChainInteraction();
    }

    @Test
    void requestWithEmptyBodyShouldNotBeStubbed() {
        setupMocks(null);
        Mono<Void> filter = preFilter.filter(exchange, chainMock);

        Mockito.verify(checkerMock, Mockito.never()).getMockRule(anyString());
        assertThat(filter).isEqualTo(empty);
        verifyChainInteraction();
    }

    @Test
    void requestNotMatchingRulesShouldNotBeStubbed() {
        setupMocks("someBodyWithoutStub", null, null);
        Mono<Void> filter = preFilter.filter(exchange, chainMock);

        Mockito.verify(checkerMock, Mockito.only()).getMockRule(anyString());
        assertThat(filter).isEqualTo(empty);
        verifyChainInteraction();
    }

    @Test
    void requestMatchingRulesShouldBeStubbed() {
        setupMocks("someBodyWithStub", "someStub", null);
        Mono<Void> filter = preFilter.filter(exchange, chainMock);

        Mockito.verify(checkerMock, Mockito.only()).getMockRule(anyString());
        assertThat(filter).isNotEqualTo(empty);
        verifyChainInteraction();
    }

    @Test
    void requestMatchingRulesWithHostShouldBeForwarding() {
        setupMocks("someBodyWithStub", "someStub", "localhost");
        Mono<Void> filter = preFilter.filter(exchange, chainMock);

        Mockito.verify(checkerMock, Mockito.only()).getMockRule(anyString());
        assertThat(filter).isEqualTo(empty);
        verifyChainInteraction();
    }

    private void setupMocks(String exchangeStubbedResponse, String checkerStubbedResponse, String ruleHost) {
        setupMocks(exchangeStubbedResponse);
        MockRule rule = null;
        if (checkerStubbedResponse != null) {
            rule = new MockRule();
            rule.setBody(checkerStubbedResponse);
            rule.setHost(ruleHost);
        }
        Mockito.when(checkerMock.getMockRule(anyString())).thenReturn(Optional.ofNullable(rule));
    }

    private void setupMocks(String exchangeStubbedResponse) {
        Mockito.when(chainMock.filter(exchange)).thenReturn(empty);
        if (exchangeStubbedResponse != null) {
            exchange.getAttributes().put(REQUEST_BODY_OBJECT, exchangeStubbedResponse);
        }
    }

    private void verifyChainInteraction() {
        Mockito.verify(chainMock).filter(exchange);
        Mockito.verifyNoMoreInteractions(chainMock);
    }
}
