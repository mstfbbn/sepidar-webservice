package com.sepidar.accounting.models.responses.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItem {

    @JsonProperty("InvoiceItemID")
    private Integer invoiceItemID;

    @JsonProperty("ItemRef")
    private Integer itemRef;

    @JsonProperty("TracingRef")
    private Integer tracingRef;

    @JsonProperty("TracingTitle")
    private String tracingTitle;

    @JsonProperty("Quantity")
    private Double quantity;

    @JsonProperty("SecondaryQuantity")
    private Double secondaryQuantity;

    @JsonProperty("Fee")
    private Double fee;

    @JsonProperty("Price")
    private Double price;

    @JsonProperty("Discount")
    private Double discount;

    @JsonProperty("Tax")
    private Double tax;

    @JsonProperty("Duty")
    private Double duty;

    @JsonProperty("Addition")
    private Double addition;

    @JsonProperty("NetPrice")
    private Double netPrice;

    @JsonProperty("DiscountInvoiceItemRef")
    private Integer discountInvoiceItemRef;

    @JsonProperty("ProductPackRef")
    private Integer productPackRef;

    @JsonProperty("ProductPackQuantity")
    private Double productPackQuantity;
}
