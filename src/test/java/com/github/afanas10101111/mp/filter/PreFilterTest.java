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
    private ServerWebExchange exchangeMock;

    @Mock
    private GatewayFilterChain chainMock;

    @Mock
    private ProxyConfig configMock;

    @Mock
    private RequestBodyChecker checkerMock;

    @InjectMocks
    private PreFilter preFilter;

    @Test
    void requestWithEmptyBodyShouldNotBeStubbed() {
        setupMocks(null);
        Mono<Void> filter = preFilter.filter(exchangeMock, chainMock);

        Mockito.verify(checkerMock, Mockito.never()).getMockRule(anyString());
        Mockito.verify(exchangeMock, Mockito.never()).getResponse();
        Mockito.verify(exchangeMock, Mockito.never()).getAttributes();
        assertThat(filter).isEqualTo(empty);
    }

    @Test
    void requestNotMatchingRulesShouldNotBeStubbed() {
        setupMocks("someBodyWithoutStub", null, null);
        Mono<Void> filter = preFilter.filter(exchangeMock, chainMock);

        Mockito.verify(checkerMock, Mockito.only()).getMockRule(anyString());
        Mockito.verify(exchangeMock, Mockito.never()).getResponse();
        Mockito.verify(exchangeMock, Mockito.never()).getAttributes();
        assertThat(filter).isEqualTo(empty);
    }

    @Test
    void requestMatchingRulesShouldBeStubbed() {
        setupMocks("someBodyWithStub", "someStub", null);
        ServerHttpResponse response = Mockito.mock(ServerHttpResponse.class);
        Mockito.when(exchangeMock.getResponse()).thenReturn(response);
        Mono<Void> filter = preFilter.filter(exchangeMock, chainMock);

        Mockito.verify(checkerMock, Mockito.only()).getMockRule(anyString());
        Mockito.verify(exchangeMock, Mockito.atLeastOnce()).getResponse();
        Mockito.verify(exchangeMock, Mockito.atLeastOnce()).getAttributes();
        assertThat(filter).isNotEqualTo(empty);
    }

    @Test
    void requestMatchingRulesWithHostShouldBeForwarding() {
        setupMocks("someBodyWithStub", "someStub", "localhost");
        Mono<Void> filter = preFilter.filter(exchangeMock, chainMock);

        Mockito.verify(checkerMock, Mockito.only()).getMockRule(anyString());
        Mockito.verify(exchangeMock, Mockito.atLeastOnce()).getAttributes();
        Mockito.verify(exchangeMock, Mockito.never()).getResponse();
        assertThat(filter).isEqualTo(empty);
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
        Mockito.when(chainMock.filter(exchangeMock)).thenReturn(empty);
        Mockito.when(exchangeMock.getAttribute(anyString())).thenReturn(exchangeStubbedResponse);
    }
}
