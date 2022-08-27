package com.github.afanas10101111.mp.filter;

import com.github.afanas10101111.mp.config.ProxyConfig;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.RequestBodyChecker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class PreFilter implements GlobalFilter {
    private static final String REQUEST_BODY_OBJECT = "cachedRequestBodyObject";
    private static final String HOST_AND_PORT_FROM_RULE_FORMAT = "%s:%d";

    private final RequestBodyChecker checker;

    private String forwardingUrl;

    @PostConstruct
    @Autowired
    private void setForwardingUrl(ProxyConfig proxyConfig) {
        forwardingUrl = proxyConfig.getUrl();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String body = exchange.getAttribute(REQUEST_BODY_OBJECT);
        log.info("filter -> request body:\n{}", body);
        if (body != null) {
            return processRequestBody(exchange, chain, body);
        }
        return justForward(exchange, chain);
    }

    private Mono<Void> processRequestBody(ServerWebExchange exchange, GatewayFilterChain chain, String body) {
        Optional<MockRule> mockRule = checker.getMockRule(body);
        log.info("processRequestBody -> found rule:\n{}", mockRule.orElse(null));
        if (mockRule.isPresent()) {
            return processMockRule(exchange, chain, mockRule.get());
        }
        return justForward(exchange, chain);
    }

    private Mono<Void> processMockRule(ServerWebExchange exchange, GatewayFilterChain chain, MockRule rule) {
        if (rule.getHost() != null) {
            return processForwardingUrlModification(exchange, chain, rule);
        } else {
            return processResponseBodyModification(exchange, chain, rule);
        }
    }

    private Mono<Void> processForwardingUrlModification(
            ServerWebExchange exchange, GatewayFilterChain chain, MockRule rule
    ) {
        String host = rule.getHost();
        int port = rule.getPort();
        exchange.getAttributes().computeIfPresent(
                ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,
                (k, v) -> UriComponentsBuilder.fromUri((URI) v)
                        .host(host)
                        .port(port)
                        .build()
                        .toUri()
        );
        log.info(
                "processForwardingUrlModification -> forwarded on:\n{}",
                String.format(HOST_AND_PORT_FROM_RULE_FORMAT, host, port)
        );
        return chain.filter(exchange);
    }

    @SneakyThrows(InterruptedException.class)
    private Mono<Void> processResponseBodyModification(
            ServerWebExchange exchange, GatewayFilterChain chain, MockRule rule
    ) {
        ServerWebExchangeUtils.setResponseStatus(exchange, rule.getStatus());
        ServerWebExchangeUtils.setAlreadyRouted(exchange);
        TimeUnit.MILLISECONDS.sleep(rule.getDelay());
        return chain.filter(exchange).then(Mono.defer(() -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, rule.getContentType());
            DataBuffer buffer = response.bufferFactory().wrap(rule.getBody().getBytes());
            return response.writeWith(Flux.just(buffer));
        }));
    }

    private Mono<Void> justForward(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("justForward -> forwarded on:\n{}", forwardingUrl);
        return chain.filter(exchange);
    }
}
