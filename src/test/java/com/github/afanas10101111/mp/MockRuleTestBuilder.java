package com.github.afanas10101111.mp;

import com.github.afanas10101111.mp.model.MockRule;
import com.github.afanas10101111.mp.model.PatternKeeper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aMockRule")
@With
public class MockRuleTestBuilder {
    Integer id = null;
    HttpStatus status = HttpStatus.OK;
    String contentType = "text/xml;charset=UTF-8";
    String body = "DEFAULT";
    List<PatternKeeper> patterns = new ArrayList<>();
    int repeatLimit = 0;

    public MockRule build() {
        MockRule rule = new MockRule();
        rule.setStatus(status);
        rule.setContentType(contentType);
        rule.setBody(body);
        rule.setPatterns(patterns);
        rule.setRepeatLimit(repeatLimit);
        return rule;
    }
}
