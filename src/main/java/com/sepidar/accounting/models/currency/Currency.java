package com.sepidar.accounting.models.currency;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Currency {

    @SerializedName("CurrencyID")
    private Integer currencyId;

    @SerializedName("Title")
    private String title;

    @SerializedName("PrecisionCount")
    private Integer precisionCount;

    @SerializedName("IsMain")
    private Boolean isMain;
}
