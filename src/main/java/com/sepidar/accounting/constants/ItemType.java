package com.sepidar.accounting.constants;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ItemType {
    PRODUCT(1),
    SERVICE(2),
    PROPERTY(3);

    private final Integer value;

    ItemType(Integer value) {
        this.value = value;
    }

    public static ItemType of(Integer value) {
        return Arrays.stream(ItemType.values()).filter(item -> item.value.equals(value)).findFirst().orElse(null);
    }
}
