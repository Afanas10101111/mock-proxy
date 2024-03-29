package com.github.afanas10101111.mp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.afanas10101111.mp.model.serializer.HttpStatusDeserializer;
import com.github.afanas10101111.mp.model.serializer.HttpStatusSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.HttpStatus;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"id", "repeatCounter"})
@Entity
@Table(name = "rules")
public class MockRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @JsonManagedReference
    @OneToMany(mappedBy = "mockRule", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PatternKeeper> patterns = new HashSet<>();

    @NotNull
    @JsonSerialize(using = HttpStatusSerializer.class)
    @JsonDeserialize(using = HttpStatusDeserializer.class)
    private HttpStatus status;

    @NotNull
    private String contentType;

    @NotNull
    private String body;

    private int delay;
    private int repeatLimit;
    private int repeatCounter;

    @Range(min = 0, max = 65535)
    private int port;
    private String host;

    @JsonIgnore
    public int getAndIncrementRepeatCounter() {
        return repeatCounter++;
    }

    public void setPatterns(Collection<PatternKeeper> patterns) {
        Set<PatternKeeper> patternSet = new HashSet<>(patterns);
        patternSet.forEach(p -> p.setMockRule(this));
        this.patterns = patternSet;
    }
}
