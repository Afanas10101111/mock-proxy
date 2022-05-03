package com.github.afanas10101111.mp.repository.converter;

import org.springframework.http.HttpStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class HttpStatusConverter implements AttributeConverter<HttpStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(HttpStatus httpStatus) {
        if (httpStatus == null) {
            return null;
        }
        return httpStatus.value();
    }

    @Override
    public HttpStatus convertToEntityAttribute(Integer statusCode) {
        if (statusCode == null) {
            return null;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
