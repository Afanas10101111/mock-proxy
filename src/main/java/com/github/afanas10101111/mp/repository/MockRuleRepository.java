package com.github.afanas10101111.mp.repository;

import com.github.afanas10101111.mp.model.MockRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockRuleRepository extends JpaRepository<MockRule, Integer> {
    List<MockRule> findAllByStub(String stub);
}
