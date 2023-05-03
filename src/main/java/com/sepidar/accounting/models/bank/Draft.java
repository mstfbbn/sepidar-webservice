package com.sepidar.accounting.models.bank;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Draft {

    @SerializedName("Date")
    private Date date;

    @SerializedName("Description")
    private String description;

    @SerializedName("Number")
    private String number;

    @SerializedName("BankAccountID")
    private Integer bankAccountId;

    @SerializedName("Amount")
    private Double amount;
}
