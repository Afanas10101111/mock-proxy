package com.github.afanas10101111.mp.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class MockRuleTo {
    public static final String STUB_VALIDATION_ERROR_MESSAGE = "stub must be presented";
    public static final String PATTERNS_VALIDATION_ERROR_MESSAGE = "at least one pattern must be presented";

    @NotNull(message = STUB_VALIDATION_ERROR_MESSAGE)
    private String stub;

    @NotEmpty(message = PATTERNS_VALIDATION_ERROR_MESSAGE)
    private List<@Valid PatternKeeperTo> patterns = new ArrayList<>();

    private int repeatLimit;
}
