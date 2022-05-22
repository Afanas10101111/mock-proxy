package com.github.afanas10101111.mp.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class ErrorTo {
    private final ErrorType type;
    private final String reason;

    @RequiredArgsConstructor
    public enum ErrorType {
        RULE("Error during mock rules administration"),
        FILE("Error during file administration"),
        PROXY("Proxy error");

        private final String description;

        @JsonValue
        public String getDescription() {
            return description;
        }
    }
}
