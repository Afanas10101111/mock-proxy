package com.github.afanas10101111.mp.service;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class RequestBodyChecker {
    private final MockRuleService service;

    @Transactional
    public Optional<String> getStubbedResponse(String body) {
        for (MockRule rule : service.getAll()) {
            boolean needToStub = false;
            for (PatternKeeper pattern : rule.getPatterns()) {
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
