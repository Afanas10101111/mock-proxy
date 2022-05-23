package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.dto.ErrorTo;
import com.github.afanas10101111.mp.dto.MockRuleTo;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.MockRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockRuleController.class)
@Import(ModelMapper.class)
@MockBean(MockRuleService.class)
class MockRuleControllerTest extends ControllerTest {
    private static final String URL = MockRuleController.URL;
    private static final String GROUP = MockRuleController.GROUP;
    private static final String ID_TO_DELETE = "/1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockRuleService serviceMock;

    @BeforeEach
    void serviceSetup() {
        super.mockMvc = mockMvc;
        Mockito.when(serviceMock.getAll()).thenReturn(RULES);
        Mockito.when(serviceMock.save(any(MockRule.class))).thenReturn(RULE);
        Mockito.when(serviceMock.saveAll(anyList())).thenReturn(RULES);
    }

    @Test
    void getAllShouldReturnAllRulesList() throws Exception {
        checkPostOrGet(MockMvcRequestBuilders.get(URL), status().isOk(), RULES_STRING);
    }

    @Test
    void addShouldAddRuleToDbAndReturnAllRulesList() throws Exception {
        checkPostOrGet(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_STRING),
                status().isCreated(),
                RULES_STRING
        );
    }

    @Test
    void addGroupShouldAddRuleListToDbAndReturnAllRulesList() throws Exception {
        checkPostOrGet(
                MockMvcRequestBuilders.post(URL + GROUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULES_STRING),
                status().isCreated(),
                RULES_STRING
        );
    }

    @Test
    void deleteShouldDeleteOneCertainRule() throws Exception {
        checkDelete(URL + ID_TO_DELETE);
    }

    @Test
    void deleteAllShouldDeleteAllRules() throws Exception {
        checkDelete(URL);
    }

    @Test
    void addingRuleWithoutStubShouldGenerateAnError() throws Exception {
        checkPostOrGet(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_WITHOUT_STUB_STRING),
                status().isBadRequest(),
                getJsonError(ErrorTo.ErrorType.RULE, MockRuleTo.BODY_VALIDATION_ERROR_MESSAGE)
        );
    }

    @Test
    void addingRuleWithoutPatternsShouldGenerateAnError() throws Exception {
        checkPostOrGet(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_WITHOUT_PATTERNS_STRING),
                status().isBadRequest(),
                getJsonError(ErrorTo.ErrorType.RULE, MockRuleTo.PATTERNS_VALIDATION_ERROR_MESSAGE)
        );
    }

    @Test
    void addingRuleWithEmptyPatternsShouldGenerateAnError() throws Exception {
        checkPostOrGet(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_WITH_EMPTY_PATTERNS_STRING),
                status().isBadRequest(),
                getJsonError(ErrorTo.ErrorType.RULE, MockRuleTo.PATTERNS_VALIDATION_ERROR_MESSAGE)
        );
    }
}
