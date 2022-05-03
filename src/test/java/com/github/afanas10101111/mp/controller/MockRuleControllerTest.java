package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.MockRuleTestBuilder;
import com.github.afanas10101111.mp.dto.MockRuleTo;
import com.github.afanas10101111.mp.dto.PatternKeeperTo;
import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
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

@WebMvcTest
@Import(ModelMapper.class)
class MockRuleControllerTest {
    private static final String URL = MockRuleController.URL;
    private static final String GROUP = MockRuleController.GROUP;
    private static final String ID_TO_DELETE = "/1";
    private static final String RULE = "{\"patterns\":[{\"pattern\":\"PATTERN\"}],\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"body\":\"STUB\",\"repeatLimit\":4}";
    private static final String RULES = "[{\"patterns\":[{\"pattern\":\"PATTERN#1\"}],\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"body\":\"STUB#1\",\"repeatLimit\":4},{\"patterns\":[{\"pattern\":\"PATTERN#2\"}],\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"body\":\"STUB#2\",\"repeatLimit\":4}]";
    private static final String RESPONSE = "[{\"id\":null,\"patterns\":[{\"pattern\":\"PATTERN!!!\"}],\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"body\":\"DEFAULT\",\"repeatLimit\":0,\"repeatCounter\":0}]";

    private static final String ERROR_RESPONSE_FORMAT = "{\"type\":\"Error during mock rules administration\",\"reason\":\"%s\"}";
    private static final String RULE_WITHOUT_STUB = "{\"patterns\":[{\"pattern\":\"PATTERN\"}],\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"repeatLimit\":4}";
    private static final String RULE_WITHOUT_PATTERNS = "{\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"body\":\"STUB\",\"repeatLimit\":4}";
    private static final String RULE_WITH_EMPTY_PATTERN = "{\"patterns\":[{\"pattern\":\"\"}],\"status\":200,\"contentType\":\"text/xml;charset=UTF-8\",\"body\":\"STUB\",\"repeatLimit\":4}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MockRuleService service;

    @BeforeEach
    private void serviceSetup() {
        PatternKeeper pattern = new PatternKeeper();
        pattern.setPattern("PATTERN!!!");
        MockRule mockRule = MockRuleTestBuilder.aMockRule()
                .withPatterns(Collections.singletonList(pattern))
                .build();
        List<MockRule> ruleList = Collections.singletonList(mockRule);
        Mockito.when(service.getAll()).thenReturn(ruleList);
        Mockito.when(service.save(any(MockRule.class))).thenReturn(mockRule);
        Mockito.when(service.saveAll(anyList())).thenReturn(ruleList);
    }

    @Test
    void getAll() throws Exception {
        checkGetAndAdd(MockMvcRequestBuilders.get(URL), status().isOk(), RESPONSE);
    }

    @Test
    void add() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(RULE),
                status().isCreated(),
                RESPONSE
        );
    }

    @Test
    void addGroup() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL + GROUP).contentType(MediaType.APPLICATION_JSON).content(RULES),
                status().isCreated(),
                RESPONSE
        );
    }

    @Test
    void delete() throws Exception {
        checkDelete(URL + ID_TO_DELETE);
    }

    @Test
    void deleteAll() throws Exception {
        checkDelete(URL);
    }

    @Test
    void addingRuleWithoutStubShouldGenerateAnError() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(RULE_WITHOUT_STUB),
                status().isBadRequest(),
                String.format(ERROR_RESPONSE_FORMAT, MockRuleTo.BODY_VALIDATION_ERROR_MESSAGE)
        );
    }

    @Test
    void addingRuleWithoutPatternsShouldGenerateAnError() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(RULE_WITHOUT_PATTERNS),
                status().isBadRequest(),
                String.format(ERROR_RESPONSE_FORMAT, MockRuleTo.PATTERNS_VALIDATION_ERROR_MESSAGE)
        );
    }

    @Test
    void addingRuleWithEmptyPatternShouldGenerateAnError() throws Exception {
        checkGetAndAdd(
                MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(RULE_WITH_EMPTY_PATTERN),
                status().isBadRequest(),
                String.format(ERROR_RESPONSE_FORMAT, PatternKeeperTo.PATTERN_VALIDATION_ERROR_MESSAGE)
        );
    }

    private void checkGetAndAdd(RequestBuilder requestBuilder, ResultMatcher responseStatus, String response) throws Exception {
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
}
