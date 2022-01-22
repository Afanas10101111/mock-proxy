package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.dto.ErrorTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MockRulesControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorTo> handleCommonException(Exception e) {
        String reason = ExceptionUtils.getRootCauseMessage(e).replaceFirst("^[^:]+: ", "");
        log.warn("handleCommonException -> {}", reason);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorTo(ErrorTo.ErrorType.RULE, reason));
    }
}
