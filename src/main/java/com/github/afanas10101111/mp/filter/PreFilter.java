package com.github.afanas10101111.mp.filter;

import com.github.afanas10101111.mp.config.ProxyConfig;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.RequestBodyChecker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class PreFilter implements GlobalFilter {
    private static final String REQUEST_BODY_OBJECT = "cachedRequestBodyObject";
    private static final String HOST_AND_PORT_FROM_RULE_FORMAT = "%s:%d";

    private final ProxyConfig proxyConfig;
    private final RequestBodyChecker checker;

    @Override
    @SneakyThrows(InterruptedException.class)
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String body = exchange.getAttribute(REQUEST_BODY_OBJECT);
        String forwardingUrl = proxyConfig.getUrl();
        if (body != null) {
            log.info("filter -> request:\n{}", body);
            Optional<MockRule> mockRule = checker.getMockRule(body);
            if (mockRule.isPresent()) {
                MockRule rule = mockRule.get();
                log.info("filter -> found rule:\n{}", rule);
                if (rule.getHost() != null) {
                    String host = rule.getHost();
                    int port = rule.getPort();
                    forwardingUrl = String.format(HOST_AND_PORT_FROM_RULE_FORMAT, host, port);
                    exchange.getAttributes().computeIfPresent(
                            ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR,
                            (k, v) -> UriComponentsBuilder.fromUri((URI)v)
                                    .host(host)
                                    .port(port)
                                    .build()
                                    .toUri()
                    );
                } else {
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
            }
        }
        log.info("filter -> forwarded on:\n{}", forwardingUrl);
        return chain.filter(exchange);
    }
}
