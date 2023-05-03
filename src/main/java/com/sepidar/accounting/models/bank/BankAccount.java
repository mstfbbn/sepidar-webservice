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
    private String diCode;

    @SerializedName("DlTitle")
    private String diTitle;

    @SerializedName("CurrencyRef")
    private Integer currencyRef;
}
