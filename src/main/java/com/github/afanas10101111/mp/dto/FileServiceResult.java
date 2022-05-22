package com.github.afanas10101111.mp.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class FileServiceResult {
    private final Operation operation;
    private final String file;

    @RequiredArgsConstructor
    public enum Operation {
        SAVE,
        LOAD,
        SAVED_FILES_NAMES
    }
}
