package com.github.afanas10101111.mp.service;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import com.github.afanas10101111.mp.repository.MockRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class RequestBodyChecker {
    private final MockRuleRepository repository;

    @Transactional
    public Optional<String> getStubbedResponse(String body) {
        List<MockRule> rules = repository.findAll();
        for (MockRule rule : rules) {
            List<PatternKeeper> patterns = rule.getPatterns();
            boolean needToStub = false;
            for (PatternKeeper pattern : patterns) {
                if (Pattern.compile(pattern.getPattern()).matcher(body).find()) {
                    needToStub = true;
                } else {
                    needToStub = false;
                    break;
                }
            }
            if (needToStub && rule.needToRepeat()) {
                return Optional.of(rule.getStub());
            }
        }
        return Optional.empty();
    }
}
