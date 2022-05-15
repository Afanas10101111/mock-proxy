package com.github.afanas10101111.mp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.afanas10101111.mp.model.MockRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
@Sql(scripts = "/sql/prepareForTest.sql", config = @SqlConfig(encoding = "UTF-8"))
@Import({MockRuleService.class, MockRuleFileService.class, ObjectMapper.class})
class MockRuleFileServiceTest {
    public static final String SAVED_FILE_NAME = "saved";
    public static final String PREPARED_FILE_NAME = "prepared";

    @Autowired
    private MockRuleFileService fileService;

    @Autowired
    private MockRuleService ruleService;

    @BeforeEach
    void prepareFile() {
        fileService.save(PREPARED_FILE_NAME);
    }

    @AfterEach
    void deleteFiles() {
        fileService.deleteAll();
    }

    @Test
    void saveShouldSaveCurrentRuleListToFile() {
        List<MockRule> rules = fileService.save(SAVED_FILE_NAME);
        assertThat(rules).isEqualTo(ruleService.getAll());
    }

    @Test
    void loadShouldAddRulesFromFileToCurrentRuleList() {
        ruleService.deleteAll();
        assertThat(ruleService.getAll()).isEmpty();
        List<MockRule> rules = fileService.load(PREPARED_FILE_NAME);
        assertThat(rules).isEqualTo(ruleService.getAll());
    }

    @Test
    void getSavedFilesNamesShouldReturnAllSavedFilesNames() {
        Set<String> savedFilesNames = fileService.getSavedFilesNames();
        assertThat(savedFilesNames).hasSize(1).contains(PREPARED_FILE_NAME);
    }

    @Test
    void deleteShouldDeleteSavedFileByName() {
        assertDoesNotThrow(() -> fileService.delete(PREPARED_FILE_NAME));
    }

    @Test
    void deleteAllShouldDeleteAllSavedFilesInAppFolder() {
        assertDoesNotThrow(() -> fileService.deleteAll());
    }

    @Test
    void viewShouldProvideCorrectRuleListFromFile() {
        List<MockRule> rules = fileService.view(PREPARED_FILE_NAME);
        assertThat(rules).isEqualTo(ruleService.getAll());
    }
}
