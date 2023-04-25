package com.sepidar.accounting.exceptions;

import lombok.Getter;

@Getter
public class SepidarGlobalException extends RuntimeException {

    private final Integer httpStatus;
    private final Integer type;

    public SepidarGlobalException(Integer httpStatus, Integer type, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.type = type;
    }
}
