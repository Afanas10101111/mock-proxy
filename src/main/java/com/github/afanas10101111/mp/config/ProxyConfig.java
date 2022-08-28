package com.github.afanas10101111.mp.config;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.regex.Pattern;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "proxy")
@Validated
public class ProxyConfig {
    private static final String WITH_BODY = "withBody";
    private static final String WITHOUT_BODY = "withoutBody";

    @NotEmpty
    private String pathPattern;

    @NotEmpty
    private String url;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder, GatewayPredicate gatewayPredicate) {
        return builder.routes()
                .route(WITH_BODY, route -> route
                        .predicate(gatewayPredicate)
                        .and()
                        .readBody(String.class, StringUtils::hasText)
                        .uri(url))
                .route(WITHOUT_BODY, route -> route
                        .predicate(gatewayPredicate)
                        .uri(url))
                .build();
    }

    @Bean
    GatewayPredicate gatewayPredicate() {
        Pattern pattern = Pattern.compile(pathPattern);
        return exchange -> {
            if (pattern.matcher(exchange.getRequest().getURI().getRawPath()).matches()) {
                exchange.getAttributes().put(
                        ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ATTR,
                        pathPattern
                );
                exchange.getAttributes().put(
                        ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR,
                        exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_PREDICATE_ROUTE_ATTR)
                );
                return true;
            } else {
                return false;
            }
        };
    }

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(new AbstractConverter<Integer, HttpStatus>() {
            @Override
            protected HttpStatus convert(Integer source) {
                return HttpStatus.valueOf(source);
            }
        });
        return mapper;
    }

    @Bean
    WebProperties.Resources webResources(WebProperties webProperties) {
        return webProperties.getResources();
    }
}
