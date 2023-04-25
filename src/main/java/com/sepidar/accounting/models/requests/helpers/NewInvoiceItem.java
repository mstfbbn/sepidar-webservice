package com.sepidar.accounting.models.requests.helpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewInvoiceItem {

    @JsonProperty("RowID")
    private Integer rowID;

    @JsonProperty("ItemRef")
    private Integer itemRef;

    @JsonProperty("tracingRef")
    private Integer tracingRef;

    @JsonProperty("StockRef")
    private Integer stockRef;

    @JsonProperty("Quantity")
    private Double quantity;

    @JsonProperty("SecondaryQuantity")
    private Double secondaryQuantity = 0.0;

    @JsonProperty("Fee")
    private Double fee;

    @JsonProperty("Price")
    private Double price;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("PriceInfoPercentDiscount")
    private Double PriceInfoPercentDiscount = 0.0;

    @JsonProperty("PriceInfoPriceDiscount")
    private Double PriceInfoPriceDiscount = 0.0;

    @JsonProperty("PriceInfoDiscountRate")
    private Double PriceInfoDiscountRate = 0.0;

    @JsonProperty("AggregateAmountPercentDiscount")
    private Double AggregateAmountPercentDiscount = 0.0;

    @JsonProperty("AggregateAmountPriceDiscount")
    private Double AggregateAmountPriceDiscount = 0.0;

    @JsonProperty("AggregateAmountDiscountRate")
    private Double AggregateAmountDiscountRate = 0.0;

    @JsonProperty("CustomerDiscount")
    private Double CustomerDiscount = 0.0;

    @JsonProperty("CustomerDiscountRate")
    private Double CustomerDiscountRate = 0.0;

    @JsonProperty("Discount")
    private Double Discount = 0.0;

    @JsonProperty("DiscountParentRef")
    private Integer DiscountParentRef;

    @JsonProperty("Tax")
    private Double Tax;

    @JsonProperty("Duty")
    private Double Duty;

    @JsonProperty("Addition")
    private Double Addition;
}
