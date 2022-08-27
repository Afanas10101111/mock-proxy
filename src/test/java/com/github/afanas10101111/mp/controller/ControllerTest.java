package com.github.afanas10101111.mp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.afanas10101111.mp.MockRuleTestBuilder;
import com.github.afanas10101111.mp.PatternKeeperTestBuilder;
import com.github.afanas10101111.mp.dto.ErrorTo;
import com.github.afanas10101111.mp.model.MockRule;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class ControllerTest {
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected static MockRule RULE;
    protected static List<MockRule> RULES;

    protected static String RULE_STRING;
    protected static String RULES_STRING;
    protected static String RULE_WITHOUT_STUB_STRING;
    protected static String RULE_WITHOUT_PATTERNS_STRING;
    protected static String RULE_WITH_EMPTY_PATTERNS_STRING;
    protected static String RULE_WITH_PORT_OUT_OF_RANGE_STRING;

    protected MockMvc mockMvc;

    @BeforeAll
    protected static void setupRequestAndResponseStrings() throws JsonProcessingException {
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
        RULE_WITH_PORT_OUT_OF_RANGE_STRING = MAPPER.writeValueAsString(MockRuleTestBuilder.aMockRule()
                .withPort(-1)
                .build());
    }

    protected void checkPostOrGet(RequestBuilder requestBuilder, ResultMatcher responseStatus, String response)
            throws Exception {
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(responseStatus)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(response));
    }

    protected void checkDelete(String url) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(url))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    protected String getJsonError(ErrorTo.ErrorType type, String reason) throws JsonProcessingException {
        return MAPPER.writeValueAsString(new ErrorTo(type, reason));
    }
}
