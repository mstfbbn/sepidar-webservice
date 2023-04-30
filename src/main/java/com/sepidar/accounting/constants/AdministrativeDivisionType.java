package com.sepidar.accounting.constants;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AdministrativeDivisionType {
    COUNTRY(1),
    PROVINCE(2),
    CITY(3),
    VILLAGE(4);

    private final Integer value;

    AdministrativeDivisionType(Integer value) {
        this.value = value;
    }

    public static AdministrativeDivisionType of(Integer value) {
        return Arrays.stream(AdministrativeDivisionType.values()).filter(item -> item.value.equals(value)).findFirst().orElse(null);
    }
}
