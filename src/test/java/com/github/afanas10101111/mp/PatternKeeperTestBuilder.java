package com.github.afanas10101111.mp;

import com.github.afanas10101111.mp.model.PatternKeeper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aPatternKeeper")
@With
public class PatternKeeperTestBuilder {
    String pattern = "DEFAULT";

    public PatternKeeper build() {
        PatternKeeper patternKeeper = new PatternKeeper();
        patternKeeper.setPattern(pattern);
        return patternKeeper;
    }
}
