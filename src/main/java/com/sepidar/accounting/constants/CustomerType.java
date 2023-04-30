package com.sepidar.accounting.constants;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CustomerType {
    NATURAL(1),
    LEGAL(2);

    private final Integer value;

    CustomerType(Integer value) {
        this.value = value;
    }

    public static CustomerType of(Integer value) {
        return Arrays.stream(CustomerType.values()).filter(item -> item.value.equals(value)).findFirst().orElse(null);
    }
}

