package com.github.afanas10101111.mp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "rules")
public class MockRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonManagedReference
    @OneToMany(mappedBy = "mockRule", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatternKeeper> patterns = new HashSet<>();

    private String stub;
    private int repeatLimit;
    private int repeatCounter;

    public boolean needToRepeat() {
        boolean needToRepeat = repeatLimit < 0 || repeatCounter++ < repeatLimit;
        if (repeatCounter > repeatLimit) {
            repeatCounter = 0;
        }
        return needToRepeat;
    }

    public void setPatterns(Collection<PatternKeeper> patterns) {
        Set<PatternKeeper> patternSet = new HashSet<>(patterns);
        patternSet.forEach(p -> p.setMockRule(this));
        this.patterns = patternSet;
    }
}
