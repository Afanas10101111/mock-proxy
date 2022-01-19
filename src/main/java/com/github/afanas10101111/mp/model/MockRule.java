package com.github.afanas10101111.mp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rules")
public class MockRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonManagedReference
    @OneToMany(mappedBy = "mockRule", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatternKeeper> patterns = new ArrayList<>();

    private String stub;
    private int counter;

    public void addPatternString(PatternKeeper patternKeeper) {
        patterns.add(patternKeeper);
        patternKeeper.setMockRule(this);
    }

    public void removePatternString(PatternKeeper patternKeeper) {
        patterns.remove(patternKeeper);
        patternKeeper.setMockRule(null);
    }
}
