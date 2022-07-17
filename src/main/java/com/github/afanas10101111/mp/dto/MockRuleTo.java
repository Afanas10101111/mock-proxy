package com.github.afanas10101111.mp.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class MockRuleTo {
    public static final String STATUS_VALIDATION_ERROR_MESSAGE = "status stub must be presented";
    public static final String CONTENT_TYPE_VALIDATION_ERROR_MESSAGE = "content type must be presented";
    public static final String BODY_VALIDATION_ERROR_MESSAGE = "body must be presented";
    public static final String PATTERNS_VALIDATION_ERROR_MESSAGE = "at least one pattern must be presented";
    public static final String PORT_VALIDATION_ERROR_MESSAGE = "ports range 0—65535";

    @NotNull(message = STATUS_VALIDATION_ERROR_MESSAGE)
    private Integer status;

    @NotNull(message = CONTENT_TYPE_VALIDATION_ERROR_MESSAGE)
    private String contentType;

    @NotNull(message = BODY_VALIDATION_ERROR_MESSAGE)
    private String body;

    @NotEmpty(message = PATTERNS_VALIDATION_ERROR_MESSAGE)
    private List<@Valid PatternKeeperTo> patterns = new ArrayList<>();

    private int delay;
    private int repeatLimit;

    @Range(min = 0, max = 65535, message = PORT_VALIDATION_ERROR_MESSAGE)
    private int port;
    private String host;
}
