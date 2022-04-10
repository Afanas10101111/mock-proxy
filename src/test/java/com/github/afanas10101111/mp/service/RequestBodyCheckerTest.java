package com.github.afanas10101111.mp.service;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RequestBodyCheckerTest {
    private static final String SUBBED_RESPONSE = "SUBBED RESPONSE";

    private static final MockRuleService service = Mockito.mock(MockRuleService.class);
    private static final RequestBodyChecker checker = new RequestBodyChecker(service);

    @Test
    void responseForNoMatchingBodyShouldNotBePresent() {
        prepareRule(1, 0, "PATTERN");
        Optional<String> response = checker.getStubbedResponse("some body without pattern");
        assertThat(response).isNotPresent();
    }

    @Test
    void responseForMatchingBodyShouldBeStubbed() {
        prepareRule(1, 0, "PATTERN");
        Optional<String> response = checker.getStubbedResponse("...PATTERN...");
        assertThat(response).contains(SUBBED_RESPONSE);
    }

    @Test
    void responseForMatchingBodyWithOverLimitRepeatCounterShouldNotBePresent() {
        prepareRule(2, 2, "PATTERN");
        Optional<String> response = checker.getStubbedResponse("...PATTERN...");
        assertThat(response).isNotPresent();
    }

    @Test
    void repeatCounterShouldBeResetAfterOverLimit() {
        prepareRule(2, 2, "PATTERN");
        checker.getStubbedResponse("...PATTERN...");
        assertThat(service.getAll().get(0).getRepeatCounter()).isZero();
    }

    @Test
    void responseForMatchingSeveralPatternsBodyShouldBeStubbed() {
        prepareRule(1, 0, "PATTERN", "^\\d*[13579]\\.");
        Optional<String> response = checker.getStubbedResponse("131...PATTERN...");
        assertThat(response).contains(SUBBED_RESPONSE);
    }

    @Test
    void responseForMatchingNotAllOfSeveralPatternsBodyShouldNotBePresent() {
        prepareRule(1, 0, "PATTERN", "^\\d*[13579]\\.");
        Optional<String> response = checker.getStubbedResponse("130...PATTERN...");
        assertThat(response).isNotPresent();
    }

    private void prepareRule(int repeatLimit, int repeatCounter, String... patterns) {
        MockRule rule = new MockRule();
        rule.setStub(SUBBED_RESPONSE);
        rule.setRepeatLimit(repeatLimit);
        rule.setRepeatCounter(repeatCounter);
        rule.setPatterns(
                Arrays.stream(patterns)
                        .map(p -> {
                            PatternKeeper patternKeeper = new PatternKeeper();
                            patternKeeper.setPattern(p);
                            return patternKeeper;
                        })
                        .collect(Collectors.toSet())
        );
        Mockito.when(service.getAll()).thenReturn(Collections.singletonList(rule));
    }
}
