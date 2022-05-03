package com.github.afanas10101111.mp.service;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestBodyCheckerTest {
    private static final String SUBBED_RESPONSE = "SUBBED RESPONSE";

    @Mock
    private MockRuleService service;

    @InjectMocks
    private RequestBodyChecker checker;

    @Test
    void responseForNoMatchingBodyShouldNotBePresent() {
        prepareRule(1, 0, "PATTERN");
        Optional<MockRule> response = checker.getMockRule("some body without pattern");
        assertThat(response).isNotPresent();
    }

    @Test
    void responseForMatchingBodyShouldBeStubbed() {
        prepareRule(1, 0, "PATTERN");
        Optional<MockRule> response = checker.getMockRule("...PATTERN...");
        assertThat(response).isPresent();
        assertThat(response.get().getBody()).contains(SUBBED_RESPONSE);
    }

    @Test
    void responseForMatchingBodyWithOverLimitRepeatCounterShouldNotBePresent() {
        prepareRule(2, 2, "PATTERN");
        Optional<MockRule> response = checker.getMockRule("...PATTERN...");
        assertThat(response).isNotPresent();
    }

    @Test
    void repeatCounterShouldBeResetAfterOverLimit() {
        prepareRule(2, 2, "PATTERN");
        checker.getMockRule("...PATTERN...");
        assertThat(service.getAll().get(0).getRepeatCounter()).isZero();
    }

    @Test
    void responseForMatchingSeveralPatternsBodyShouldBeStubbed() {
        prepareRule(1, 0, "PATTERN", "^\\d*[13579]\\.");
        Optional<MockRule> response = checker.getMockRule("131...PATTERN...");
        assertThat(response).isPresent();
        assertThat(response.get().getBody()).contains(SUBBED_RESPONSE);
    }

    @Test
    void responseForMatchingNotAllOfSeveralPatternsBodyShouldNotBePresent() {
        prepareRule(1, 0, "PATTERN", "^\\d*[13579]\\.");
        Optional<MockRule> response = checker.getMockRule("130...PATTERN...");
        assertThat(response).isNotPresent();
    }

    private void prepareRule(int repeatLimit, int repeatCounter, String... patterns) {
        MockRule rule = new MockRule();
        rule.setBody(SUBBED_RESPONSE);
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
