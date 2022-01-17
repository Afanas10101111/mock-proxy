package com.github.afanas10101111.mp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestBodyChecker {

    @Value("${request.body.part}")
    private String requestBodyPart;
    private String stubbedResponseBody;

    @PostConstruct
    private void initStubs() throws IOException {
        stubbedResponseBody = new BufferedReader(new InputStreamReader(
                new ClassPathResource("static/body.xml").getInputStream()
        ))
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public Optional<String> getStubbedResponse(String body) {
        if (body.contains(requestBodyPart)) {
            return Optional.of(stubbedResponseBody);
        }
        return Optional.empty();
    }
}
