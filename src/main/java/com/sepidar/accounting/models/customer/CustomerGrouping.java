package com.sepidar.accounting.models.customer;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerGrouping {

    @SerializedName("CustomerGroupingID")
    private Integer groupingId;

    @SerializedName("Code")
    private Integer code;

    @SerializedName("Title")
    private String title;
}
