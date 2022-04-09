package com.github.afanas10101111.mp.service;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.repository.MockRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MockRuleService {
    private final MockRuleRepository repository;

    public MockRule save(@NotNull MockRule newOne) {
        List<MockRule> allByStub = repository.findAllByStub(newOne.getStub());
        for (MockRule existing : allByStub) {
            if (existing.equals(newOne)) {
                return existing;
            }
        }
        return repository.save(newOne);
    }

    public List<MockRule> saveAll(@NotNull List<MockRule> mockRules) {
        return mockRules.stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public List<MockRule> getAll() {
        return repository.findAll();
    }
}
