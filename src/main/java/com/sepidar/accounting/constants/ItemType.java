package com.sepidar.accounting.constants;

public enum ItemType {

    PRODUCT(1),
    SERVICE(2),
    PROPERTY(3);

    private final Integer value;

    ItemType(Integer value) {
        this.value = value;
    }
}
