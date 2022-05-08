package com.github.afanas10101111.mp.service;

import com.github.afanas10101111.mp.MockRuleTestBuilder;
import com.github.afanas10101111.mp.model.MockRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

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
    static void prepareRuleCopyFromDB() {
        preparedRule = MockRuleTestBuilder.aMockRule().build();
    }

    @Test
    void saveShouldSaveOneCertainRule() {
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES);
        MockRule newOne = MockRuleTestBuilder.aMockRule().withRepeatLimit(1).build();
        MockRule newOnesCopyForCompare = MockRuleTestBuilder.aMockRule().withRepeatLimit(1).build();

        MockRule saved = service.save(newOne);
        assertThat(saved).isEqualTo(newOnesCopyForCompare);
        List<MockRule> all = service.getAll();
        assertThat(all).hasSize(INITIAL_COUNT_OF_RULES + 1);
    }

    @Test
    void duplicatedRulesShouldNotBeSaved() {
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES);
        MockRule rule = MockRuleTestBuilder.aMockRule().withRepeatLimit(1).build();
        MockRule duplicate = MockRuleTestBuilder.aMockRule().withRepeatLimit(1).build();

        service.save(rule);
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES + 1);
        service.save(duplicate);
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES + 1);
    }

    @Test
    void saveAllShouldSaveAllNewList() {
        assertThat(service.getAll()).hasSize(INITIAL_COUNT_OF_RULES);

        int ruleListToSaveSize = 6;
        List<MockRule> ruleListToSave = MockRuleTestBuilder.aMockRule().buildList(ruleListToSaveSize);
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
