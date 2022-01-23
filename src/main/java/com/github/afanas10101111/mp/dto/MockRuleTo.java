package com.github.afanas10101111.mp.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MockRuleTo {
    private List<PatternKeeperTo> patterns = new ArrayList<>();
    private String stub;
    private int repeatLimit;
}
