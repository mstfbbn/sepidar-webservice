package com.sepidar.accounting.constants;

import lombok.Getter;

@Getter
public enum ErrorType {

    BUSINESS(0),
    APPLICATION(1);

    private final Integer value;

    ErrorType(Integer value) {
        this.value = value;
    }
}
