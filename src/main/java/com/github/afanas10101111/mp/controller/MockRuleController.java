package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import com.github.afanas10101111.mp.repository.MockRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/_admin_config")
public class MockRuleController {
    private final MockRuleRepository repository;

    @GetMapping
    public List<MockRule> init() {
        PatternKeeper pattern = new PatternKeeper();
        pattern.setPattern("1234");

        MockRule rule = new MockRule();
        rule.setStub("<Response><Status>1</Status></Response>");
        rule.setCounter(8);
        rule.addPatternString(pattern);

        repository.save(rule);

        return repository.findAll();
    }
}
