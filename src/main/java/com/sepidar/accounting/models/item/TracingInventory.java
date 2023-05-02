package com.sepidar.accounting.models.item;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TracingInventory {

    @SerializedName("TracingRef")
    private Integer tracingRef;

    @SerializedName("Inventory")
    private String inventory;
}
