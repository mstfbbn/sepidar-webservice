package com.sepidar.accounting.constants;

public enum SaleMarketType {

    DOMESTIC(1),
    EXPORT(2);

    private final Integer value;

    SaleMarketType(Integer value) {
        this.value = value;
    }
}
