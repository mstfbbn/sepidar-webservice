package com.sepidar.accounting.models.invoice;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemNew {

    @SerializedName("RowID")
    private Integer rowID;

    @SerializedName("ItemRef")
    private Integer itemRef;

    @SerializedName("tracingRef")
    private Integer tracingRef;

    @SerializedName("StockRef")
    private Integer stockRef;

    @SerializedName("Quantity")
    private Double quantity;

    @SerializedName("SecondaryQuantity")
    private Double secondaryQuantity = 0.0;

    @SerializedName("Fee")
    private Double fee;

    @SerializedName("Price")
    private Double price;

    @SerializedName("Description")
    private String description;

    @SerializedName("PriceInfoPercentDiscount")
    private Double PriceInfoPercentDiscount = 0.0;

    @SerializedName("PriceInfoPriceDiscount")
    private Double PriceInfoPriceDiscount = 0.0;

    @SerializedName("PriceInfoDiscountRate")
    private Double PriceInfoDiscountRate = 0.0;

    @SerializedName("AggregateAmountPercentDiscount")
    private Double AggregateAmountPercentDiscount = 0.0;

    @SerializedName("AggregateAmountPriceDiscount")
    private Double AggregateAmountPriceDiscount = 0.0;

    @SerializedName("AggregateAmountDiscountRate")
    private Double AggregateAmountDiscountRate = 0.0;

    @SerializedName("CustomerDiscount")
    private Double CustomerDiscount = 0.0;

    @SerializedName("CustomerDiscountRate")
    private Double CustomerDiscountRate = 0.0;

    @SerializedName("Discount")
    private Double Discount = 0.0;

    @SerializedName("DiscountParentRef")
    private Integer DiscountParentRef;

    @SerializedName("Tax")
    private Double Tax;

    @SerializedName("Duty")
    private Double Duty;

    @SerializedName("Addition")
    private Double Addition;
}
