package com.github.afanas10101111.mp.util;

import com.github.afanas10101111.mp.model.MockRule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockRuleUtil {
    public static boolean checkIsRepeatNeededAndHandleRepeatCounter(MockRule rule) {
        int repeatLimit = rule.getRepeatLimit();
        if (repeatLimit < 0) {
            return true;
        }
        int repeatCounter = rule.getAndIncrementRepeatCounter();
        if (rule.getRepeatCounter() > repeatLimit) {
            rule.setRepeatCounter(0);
        }
        return repeatCounter < repeatLimit;
    }
}
