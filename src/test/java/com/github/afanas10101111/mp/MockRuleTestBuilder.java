package com.github.afanas10101111.mp;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aMockRule")
@With
public class MockRuleTestBuilder {
    HttpStatus status = HttpStatus.OK;
    String contentType = "text/xml;charset=UTF-8";
    String body = "DEFAULT";
    List<PatternKeeper> patterns = Collections.singletonList(PatternKeeperTestBuilder.aPatternKeeper().build());
    int repeatLimit = 0;
    int repeatCounter = 0;

    public MockRule build() {
        MockRule rule = new MockRule();
        rule.setStatus(status);
        rule.setContentType(contentType);
        rule.setBody(body);
        rule.setPatterns(patterns);
        rule.setRepeatLimit(repeatLimit);
        rule.setRepeatCounter(repeatCounter);
        return rule;
    }

    public  List<MockRule> buildList(int listSize) {
        List<MockRule> rules = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            rules.add(MockRuleTestBuilder.aMockRule()
                    .withPatterns(Collections.singletonList(PatternKeeperTestBuilder.aPatternKeeper()
                            .withPattern("PATTERN#" + i)
                            .build()))
                    .withBody("STUB#" + i)
                    .build());
        }
        return rules;
    }
}
