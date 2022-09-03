package com.github.afanas10101111.mp.config;

import com.github.afanas10101111.mp.controller.MockRuleController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(MockRuleController.class.getPackage().getName()))
                .paths(PathSelectors.regex(MockRuleController.URL + ".*"))
                .build();
    }
}
