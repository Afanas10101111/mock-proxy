package com.github.afanas10101111.mp.filter;

import com.github.afanas10101111.mp.service.RequestBodyChecker;
import com.github.afanas10101111.mp.config.ProxyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PreFilter implements GlobalFilter {
    private static final String CONTENT_TYPE = "text/xml;charset=UTF-8";
    private static final String REQUEST_BODY_OBJECT = "cachedRequestBodyObject";

    private final ProxyConfig proxyConfig;
    private final RequestBodyChecker checker;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String body = exchange.getAttribute(REQUEST_BODY_OBJECT);
        if (body != null) {
            log.info("filter -> request:\n{}", body);
            Optional<String> stub = checker.getStubbedResponse(body);
            if (stub.isPresent()) {
                String stubString = stub.get();
                log.info("filter -> stubbed response:\n{}", stubString);
                ServerWebExchangeUtils.setResponseStatus(exchange, HttpStatus.OK);
                ServerWebExchangeUtils.setAlreadyRouted(exchange);
                return chain.filter(exchange).then(Mono.defer(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
                    DataBuffer buffer = response.bufferFactory().wrap(stubString.getBytes());
                    return response.writeWith(Flux.just(buffer));
                }));
            }
        }
        log.info("filter -> forwarded on:\n{}", proxyConfig.getUrl());
        return chain.filter(exchange);
    }
}
