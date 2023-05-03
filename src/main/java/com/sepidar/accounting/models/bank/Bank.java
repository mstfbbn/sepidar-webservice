package com.sepidar.accounting.models.bank;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bank {

    @SerializedName("BankID")
    private Integer bankId;

    @SerializedName("Title")
    private String title;
}
