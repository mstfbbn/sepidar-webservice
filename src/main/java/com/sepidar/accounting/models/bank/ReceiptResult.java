package com.sepidar.accounting.models.bank;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptResult {

    @SerializedName("ReceiptID")
    private Integer receiptId;

    @SerializedName("Guid")
    private String guid;
}
