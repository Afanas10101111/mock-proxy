package com.github.afanas10101111.mp.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PatternKeeperTo {
    public static final String PATTERN_VALIDATION_ERROR_MESSAGE = "pattern must not be empty";

    @NotEmpty(message = PATTERN_VALIDATION_ERROR_MESSAGE)
    private String pattern;
}
