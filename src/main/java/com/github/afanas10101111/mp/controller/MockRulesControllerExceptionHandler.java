package com.github.afanas10101111.mp.controller;

import com.github.afanas10101111.mp.dto.ErrorTo;
import com.github.afanas10101111.mp.service.exception.SavedFileAccessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@Slf4j
@RestControllerAdvice
public class MockRulesControllerExceptionHandler {

    @ExceptionHandler(SavedFileAccessException.class)
    ResponseEntity<ErrorTo> handleSavedFileAccessException(SavedFileAccessException e) {
        String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
        log.warn("handleSavedFileAccessException -> {}", rootCauseMessage);
        return getErrorResponseEntity(ErrorTo.ErrorType.FILE, rootCauseMessage);
    }

    @ExceptionHandler({WebExchangeBindException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ErrorTo> handleWebExchangeBindException(Exception e) {
        String rootCauseMessage = ExceptionUtils.getRootCauseMessage(e);
        String reason = rootCauseMessage
                .substring(rootCauseMessage.lastIndexOf("["))
                .replaceAll("[\\[\\]]", "")
                .trim();
        log.warn("handleWebExchangeBindException -> {}", reason);
        return getErrorResponseEntity(ErrorTo.ErrorType.RULE, reason);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorTo> handleCommonException(Exception e) {
        String reason = ExceptionUtils.getRootCauseMessage(e).replaceFirst("^[^:]+: ", "");
        log.warn("handleCommonException -> {}", reason);
        return getErrorResponseEntity(ErrorTo.ErrorType.RULE, reason);
    }

    private ResponseEntity<ErrorTo> getErrorResponseEntity(ErrorTo.ErrorType type, String reason) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorTo(type, reason));
    }
}
