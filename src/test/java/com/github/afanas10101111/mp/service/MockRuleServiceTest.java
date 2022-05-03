package com.github.afanas10101111.mp.service;

import com.github.afanas10101111.mp.MockRuleTestBuilder;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/sql/prepareForTest.sql", config = @SqlConfig(encoding = "UTF-8"))
@Import(MockRuleService.class)
class MockRuleServiceTest {
    private static final int INITIAL_COUNT_OF_RULES = 2;

    private static MockRule preparedRule;

    @Autowired
    private MockRuleService service;

    @BeforeAll
    private static void prepareRuleCopyFromDB() {
        PatternKeeper pattern = new PatternKeeper();
        pattern.setPattern("PATTERN#0");
        preparedRule = MockRuleTestBuilder.aMockRule()
                .withId(INITIAL_COUNT_OF_RULES - 1)
                .withBody("SUBBED RESPONSE #0")
                .withPatterns(Collections.singletonList(pattern))
                .withRepeatLimit(4)
                .build();
    }

    private static List<MockRule> getNewRules(int count) {
        List<MockRule> rules = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            PatternKeeper pattern = new PatternKeeper();
            pattern.setPattern("PATTERN#" + i);
            rules.add(MockRuleTestBuilder.aMockRule()
                    .withBody("SUBBED RESPONSE #" + i)
                    .withPatterns(Collections.singletonList(pattern))
                    .withRepeatLimit(88)
                    .build());
        }
        return rules;
    }

    @Test
    void saveShouldSaveOneCertainRule() {
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES);
        MockRule newOne = getNewRules(3).get(2);
        String newOneStub = newOne.getBody();
        int newOneRepeatLimit = newOne.getRepeatLimit();
        int newOneRepeatCounter = newOne.getRepeatCounter();

        MockRule saved = service.save(newOne);
        assertThat(saved.getBody()).isEqualTo(newOneStub);
        assertThat(saved.getRepeatLimit()).isEqualTo(newOneRepeatLimit);
        assertThat(saved.getRepeatCounter()).isEqualTo(newOneRepeatCounter);
        List<MockRule> all = service.getAll();
        assertThat(all).hasSize(INITIAL_COUNT_OF_RULES + 1);
    }

    @Test
    void duplicatedRulesShouldNotBeSaved() {
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES);
        MockRule rule = getNewRules(1).get(0);
        MockRule duplicate = getNewRules(1).get(0);

        service.save(rule);
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES + 1);
        service.save(duplicate);
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES + 1);
    }

    @Test
    void saveAllShouldSaveAllNewList() {
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES);

        int ruleListToSaveSize = 6;
        List<MockRule> ruleListToSave = getNewRules(ruleListToSaveSize);
        List<MockRule> savedRules = service.saveAll(ruleListToSave);
        assertThat(savedRules).hasSize(ruleListToSaveSize);
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES + ruleListToSaveSize);
    }

    @Test
    void deleteShouldDeleteOneCertainRule() {
        List<MockRule> all = service.getAll();
        assertThat(all).hasSize(INITIAL_COUNT_OF_RULES);
        assertThat(all.get(0)).isEqualTo(preparedRule);
        service.delete(1);

        all = service.getAll();
        assertThat(all).hasSize(INITIAL_COUNT_OF_RULES - 1);
        assertThat(all.get(0)).isNotEqualTo(preparedRule);
    }

    @Test
    void deleteAllShouldDeleteAllRules() {
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES);
        service.deleteAll();
        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getAllShouldReturnTwoRules() {
        List<MockRule> all = service.getAll();
        assertThat(all).hasSize(INITIAL_COUNT_OF_RULES);
        assertThat(all.get(0)).isEqualTo(preparedRule);
    }
}
