package com.github.afanas10101111.mp.repository;

import com.github.afanas10101111.mp.model.MockRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface MockRuleRepository extends JpaRepository<MockRule, Integer> {
    List<MockRule> findAllByBody(String body);
}
