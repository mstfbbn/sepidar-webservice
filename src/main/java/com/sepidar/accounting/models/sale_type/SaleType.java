package com.sepidar.accounting.models.sale_type;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleType {

    @SerializedName("SaleTypeID")
    private Integer saleTypeId;

    @SerializedName("Title")
    private String title;

    @SerializedName("Market")
    private Integer market;
}
