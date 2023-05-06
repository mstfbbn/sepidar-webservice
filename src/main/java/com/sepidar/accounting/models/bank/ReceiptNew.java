package com.sepidar.accounting.models.bank;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ReceiptNew {

    @SerializedName("Guid")
    private String guid;

    @SerializedName("Date")
    private Date date;

    @SerializedName("Description")
    private String description;

    @SerializedName("Discount")
    private Double discount;

    @SerializedName("InvoiceID")
    private Integer invoiceId;

    @SerializedName("Drafts")
    private List<Draft> drafts;
}
