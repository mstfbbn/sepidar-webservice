package com.sepidar.accounting.models.item;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory {

    @SerializedName("ItemRef")
    private Integer itemRef;

    @SerializedName("TracingRef")
    private Integer tracingRef;

    @SerializedName("StocksRef")
    private Integer stocksRef; // TODO: check name

    @SerializedName("Quantity")
    private Double quantity;
}
