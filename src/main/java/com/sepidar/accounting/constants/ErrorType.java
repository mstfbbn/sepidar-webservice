package com.sepidar.accounting.constants;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorType {
    BUSINESS(0),
    APPLICATION(1);

    private final Integer value;

    ErrorType(Integer value) {
        this.value = value;
    }

    public static ErrorType of(Integer value) {
        return Arrays.stream(ErrorType.values()).filter(item -> item.value.equals(value)).findFirst().orElse(null);
    }
}
