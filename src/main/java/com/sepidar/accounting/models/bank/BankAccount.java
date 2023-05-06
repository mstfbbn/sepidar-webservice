package com.sepidar.accounting.models.bank;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccount {

    @SerializedName("BankAccountID")
    private Integer bankAccountId;

    @SerializedName("DlCode")
    private String dlCode;

    @SerializedName("DlTitle")
    private String dlTitle;

    @SerializedName("CurrencyRef")
    private Integer currencyRef;
}
