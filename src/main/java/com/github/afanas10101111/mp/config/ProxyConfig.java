package com.github.afanas10101111.mp.config;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {
    private static final String WITH_BODY = "withBody";
    private static final String WITHOUT_BODY = "withoutBody";

    private String path;
    private String url;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(WITH_BODY, route -> route
                        .path(path)
                        .and()
                        .readBody(String.class, StringUtils::hasText)
                        .uri(url))
                .route(WITHOUT_BODY, route -> route
                        .path(path)
                        .uri(url))
                .build();
    }

    @Bean
    WebProperties.Resources webResources(WebProperties webProperties) {
        return webProperties.getResources();
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
}
