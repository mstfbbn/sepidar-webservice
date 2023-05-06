package com.sepidar.accounting.models.bank;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
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
