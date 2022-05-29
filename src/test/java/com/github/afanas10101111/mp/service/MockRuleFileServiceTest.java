package com.github.afanas10101111.mp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.afanas10101111.mp.MockRuleTestBuilder;
import com.github.afanas10101111.mp.model.MockRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(classes = {MockRuleFileService.class, ObjectMapper.class})
@MockBean(MockRuleService.class)
class MockRuleFileServiceTest {
    private static final String SAVED_NAME = "saved";
    private static final String FIRST_NAME = "first";
    private static final String SECOND_NAME = "second";
    private static final List<MockRule> rules = MockRuleTestBuilder.aMockRule().buildList(4);

    @Autowired
    private MockRuleService ruleServiceMock;

    @Autowired
    private MockRuleFileService fileService;

    @BeforeEach
    void prepare() {
        Mockito.when(ruleServiceMock.getAll()).thenReturn(new ArrayList<>(rules));
        fileService.save(FIRST_NAME);
        fileService.save(SECOND_NAME);
    }

    @AfterEach
    void cleanup() {
        fileService.deleteAll();
    }

    @Test
    void getFilePathStringShouldReturnCorrectPath() {
        String filePathString = fileService.getFilePathString(FIRST_NAME);
        assertThat(filePathString).endsWith(FIRST_NAME + MockRuleFileService.FILE_EXTENSION);
    }

    @Test
    void saveShouldSaveCurrentRuleListToFile() {
        List<MockRule> result = fileService.save(SAVED_NAME);
        assertThat(result).isEqualTo(rules);
        assertThat(fileService.getSavedFilesNames()).containsOnly(SAVED_NAME, FIRST_NAME, SECOND_NAME);
    }

    @Test
    void loadShouldAddRulesFromFileToCurrentRuleList() {
        List<MockRule> result = fileService.load(FIRST_NAME);
        assertThat(result).isEqualTo(rules);
        Mockito.verify(ruleServiceMock, Mockito.times(1)).saveAll(rules);
    }

    @Test
    void getSavedFilesNamesShouldReturnAllSavedFilesNames() {
        Set<String> result = fileService.getSavedFilesNames();
        assertThat(result).containsOnly(FIRST_NAME, SECOND_NAME);
    }

    @Test
    void deleteShouldDeleteSavedFileByName() {
        assertDoesNotThrow(() -> fileService.delete(FIRST_NAME));
        assertThat(fileService.getSavedFilesNames()).containsOnly(SECOND_NAME);
    }

    @Test
    void deleteAllShouldDeleteAllSavedFilesInAppFolder() {
        assertDoesNotThrow(() -> fileService.deleteAll());
        assertThat(fileService.getSavedFilesNames()).isEmpty();
    }

    @Test
    void viewShouldProvideCorrectRuleListFromFile() {
        List<MockRule> result = fileService.view(FIRST_NAME);
        assertThat(result).isEqualTo(rules);
    }
}
