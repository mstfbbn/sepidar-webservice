package com.sepidar.accounting.constants;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SaleMarketType {
    DOMESTIC(1),
    EXPORT(2);

    private final Integer value;

    SaleMarketType(Integer value) {
        this.value = value;
    }

    public static SaleMarketType of(Integer value) {
        return Arrays.stream(SaleMarketType.values()).filter(item -> item.value.equals(value)).findFirst().orElse(null);
    }
}
