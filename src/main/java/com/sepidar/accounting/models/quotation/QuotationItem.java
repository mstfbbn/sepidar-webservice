package com.sepidar.accounting.models.quotation;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotationItem {

    @SerializedName("QuotationItemID")
    private Integer quotationItemId;

    @SerializedName("RowID")
    private Integer rowId;

    @SerializedName("ItemRef")
    private Integer itemRef;

    @SerializedName("TracingRef")
    private Integer tracingRef;

    @SerializedName("StockRef")
    private Integer stockRef;

    @SerializedName("Quantity ")
    private Double quantity;

    @SerializedName("SecondaryQuantity")
    private Double secondaryQuantity;

    @SerializedName("Fee")
    private Double fee;

    @SerializedName("Price")
    private Double price;

    @SerializedName("Description")
    private String description;

    @SerializedName("PriceInfoPercentDiscount")
    private Double priceInfoPercentDiscount;

    @SerializedName("PriceInfoPriceDiscount")
    private Double priceInfoPriceDiscount;

    @SerializedName("PriceInfoDiscountRate")
    private Double priceInfoDiscountRate;

    @SerializedName("AggregateAmountPercentDiscount")
    private Double aggregateAmountPercentDiscount;

    @SerializedName("AggregateAmountPriceDiscount")
    private Double aggregateAmountPriceDiscount;

    @SerializedName("AggregateAmountDiscountRate")
    private Double aggregateAmountDiscountRate;

    @SerializedName("CustomerDiscount")
    private Double customerDiscount;

    @SerializedName("CustomerDiscountRate")
    private Double customerDiscountRate;

    @SerializedName("Discount")
    private Double discount;

    @SerializedName("DiscountParentRef")
    private Integer discountParentRef;

    @SerializedName("Tax")
    private Double tax;

    @SerializedName("Duty")
    private Double duty;

    @SerializedName("Addition")
    private Double addition;

    @SerializedName("NetPrice")
    private Double netPrice;
}
