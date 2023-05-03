package com.sepidar.accounting.models.quotation;

import com.google.gson.annotations.SerializedName;
import com.sepidar.accounting.models.common.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotationBatchResult {

    @SerializedName("StatusCode")
    private Integer statusCode;

    @SerializedName("Result")
    private Quotation result;

    @SerializedName("Error")
    private ErrorResponse error;
}
