package com.github.afanas10101111.mp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.afanas10101111.mp.MockRuleTestBuilder;
import com.github.afanas10101111.mp.PatternKeeperTestBuilder;
import com.github.afanas10101111.mp.dto.ErrorTo;
import com.github.afanas10101111.mp.dto.MockRuleTo;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.service.MockRuleService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockRuleController.class)
@Import(ModelMapper.class)
@MockBean(MockRuleService.class)
class MockRuleControllerTest {
    private static final String URL = MockRuleController.URL;
    private static final String GROUP = MockRuleController.GROUP;
    private static final String ID_TO_DELETE = "/1";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static MockRule RULE;
    private static List<MockRule> RULES;

    private static String RULE_STRING;
    private static String RULES_STRING;
    private static String RULE_WITHOUT_STUB_STRING;
    private static String RULE_WITHOUT_PATTERNS_STRING;
    private static String RULE_WITH_EMPTY_PATTERNS_STRING;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockRuleService service;

    @BeforeAll
    static void setupRequestAndResponseStrings() throws JsonProcessingException {
        RULE = MockRuleTestBuilder.aMockRule()
                .withPatterns(Collections.singletonList(PatternKeeperTestBuilder.aPatternKeeper()
                        .withPattern("PATTERN")
                        .build()))
                .withStatus(HttpStatus.OK)
                .withContentType("text/xml;charset=UTF-8")
                .withBody("body")
                .withRepeatLimit(4)
                .build();
        RULES = MockRuleTestBuilder.aMockRule().buildList(2);

        RULE_STRING = MAPPER.writeValueAsString(RULE);
        RULES_STRING = MAPPER.writeValueAsString(RULES);
        RULE_WITHOUT_STUB_STRING = MAPPER.writeValueAsString(MockRuleTestBuilder.aMockRule()
                .withBody(null)
                .build());
        RULE_WITHOUT_PATTERNS_STRING
                = "{\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"body\":\"STUB\",\"repeatLimit\":4}";
        RULE_WITH_EMPTY_PATTERNS_STRING = MAPPER.writeValueAsString(MockRuleTestBuilder.aMockRule()
                .withPatterns(Collections.emptyList())
                .build());
    }

    @BeforeEach
    void serviceSetup() {
        Mockito.when(service.getAll()).thenReturn(RULES);
        Mockito.when(service.save(any(MockRule.class))).thenReturn(RULE);
        Mockito.when(service.saveAll(anyList())).thenReturn(RULES);
    }

    @Test
    void getAllShouldReturnAllRulesList() throws Exception {
        checkGetAndAdd(MockMvcRequestBuilders.get(URL), status().isOk(), RULES_STRING);
    }

    @Test
    void addShouldAddRuleToDbAndReturnAllRulesList() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_STRING),
                status().isCreated(),
                RULES_STRING
        );
    }

    @Test
    void addGroupShouldAddRuleListToDbAndReturnAllRulesList() throws Exception {
        checkGetAndAdd(
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
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_WITHOUT_STUB_STRING),
                status().isBadRequest(),
                getErrorString(MockRuleTo.BODY_VALIDATION_ERROR_MESSAGE)
        );
    }

    @Test
    void addingRuleWithoutPatternsShouldGenerateAnError() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_WITHOUT_PATTERNS_STRING),
                status().isBadRequest(),
                getErrorString(MockRuleTo.PATTERNS_VALIDATION_ERROR_MESSAGE)
        );
    }

    @Test
    void addingRuleWithEmptyPatternsShouldGenerateAnError() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RULE_WITH_EMPTY_PATTERNS_STRING),
                status().isBadRequest(),
                getErrorString(MockRuleTo.PATTERNS_VALIDATION_ERROR_MESSAGE)
        );
    }

    private void checkGetAndAdd(RequestBuilder requestBuilder, ResultMatcher responseStatus, String response)
            throws Exception {
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(responseStatus)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(response));
    }

    private void checkDelete(String url) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    private String getErrorString(String reason) throws JsonProcessingException {
        return MAPPER.writeValueAsString(new ErrorTo(ErrorTo.ErrorType.RULE, reason));
    }
}
