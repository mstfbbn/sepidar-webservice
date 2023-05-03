package com.sepidar.accounting.models.invoice;

import com.google.gson.annotations.SerializedName;
import com.sepidar.accounting.models.common.ErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceBatchResult {

    @SerializedName("StatusCode")
    private Integer statusCode;

    @SerializedName("Result")
    private Invoice result;

    @SerializedName("Error")
    private ErrorResponse error;
}
