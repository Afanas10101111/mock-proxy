package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.config.ProxyConfig;
import com.github.afanas10101111.mp.dto.ErrorTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Component
public class ErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {
    private static final String REASON_FORMAT = "%s unavailable";

    private final ProxyConfig proxyConfig;

    public ErrorWebExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties.Resources resources,
            ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer,
            ProxyConfig proxyConfig
    ) {
        super(errorAttributes, resources, applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        this.proxyConfig = proxyConfig;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        String reason = String.format(REASON_FORMAT, proxyConfig.getUrl());
        log.warn("getRoutingFunction -> {}", reason);
        return RouterFunctions.route(
                RequestPredicates.all(),
                request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new ErrorTo(ErrorTo.ErrorType.PROXY, reason)))
        );
    }
}
