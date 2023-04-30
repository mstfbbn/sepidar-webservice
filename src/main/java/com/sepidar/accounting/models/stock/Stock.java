package com.sepidar.accounting.models.stock;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stock {

    @SerializedName("StockID")
    private Integer stockId;

    @SerializedName("Code")
    private Integer code;

    @SerializedName("Title")
    private String title;

    @SerializedName("IsActive")
    private Boolean isActive;
}
