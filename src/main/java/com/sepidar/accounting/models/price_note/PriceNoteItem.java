package com.sepidar.accounting.models.price_note;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceNoteItem {

    @SerializedName("PriceNoteItemID")
    private Integer priceNoteItemId;

    @SerializedName("SaleTypeRef")
    private Integer saleTypeRef;

    @SerializedName("ItemRef")
    private Integer itemRef;

    @SerializedName("TracingRef")
    private Integer tracingRef;

    @SerializedName("UnitRef")
    private Integer unitRef;

    @SerializedName("Fee")
    private Double fee;

    @SerializedName("CanChangeInvoiceFee")
    private Boolean canChangeInvoiceFee;

    @SerializedName("CanChangeInvoiceDiscount")
    private Boolean canChangeInvoiceDiscount;

    @SerializedName("CustomerGroupingRef")
    private Integer customerGroupingRef;

    @SerializedName("UpperMargin")
    private Double upperMargin;

    @SerializedName("LowerMargin")
    private Double lowerMargin;

    @SerializedName("AdditionRate")
    private Double additionRate;
}
