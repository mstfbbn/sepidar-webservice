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

    @SerializedName("StockeRef")
    private Integer stocksRef;

    @SerializedName("Quantity")
    private Double quantity;
}
