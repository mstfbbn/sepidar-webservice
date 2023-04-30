package com.sepidar.accounting.models.invoice;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItem {

    @SerializedName("InvoiceItemID")
    private Integer invoiceItemID;

    @SerializedName("ItemRef")
    private Integer itemRef;

    @SerializedName("TracingRef")
    private Integer tracingRef;

    @SerializedName("TracingTitle")
    private String tracingTitle;

    @SerializedName("Quantity")
    private Double quantity;

    @SerializedName("SecondaryQuantity")
    private Double secondaryQuantity;

    @SerializedName("Fee")
    private Double fee;

    @SerializedName("Price")
    private Double price;

    @SerializedName("Discount")
    private Double discount;

    @SerializedName("Tax")
    private Double tax;

    @SerializedName("Duty")
    private Double duty;

    @SerializedName("Addition")
    private Double addition;

    @SerializedName("NetPrice")
    private Double netPrice;

    @SerializedName("DiscountInvoiceItemRef")
    private Integer discountInvoiceItemRef;

    @SerializedName("ProductPackRef")
    private Integer productPackRef;

    @SerializedName("ProductPackQuantity")
    private Double productPackQuantity;
}
