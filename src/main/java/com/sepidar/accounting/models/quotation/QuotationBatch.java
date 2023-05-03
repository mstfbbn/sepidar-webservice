package com.sepidar.accounting.models.quotation;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class QuotationBatch {

    @SerializedName("GUID")
    private String guid;

    @SerializedName("CurrencyRef")
    private Integer currencyRef;

    @SerializedName("Rate")
    private Double rate;

    @SerializedName("Date")
    private Date date;

    @SerializedName("ExpirationDate")
    private Date expirationDate;

    @SerializedName("CustomerRef")
    private Integer customerRef;

    @SerializedName("AddressRef")
    private Integer addressRef;

    @SerializedName("SaleTypeRef")
    private Integer saleTypeRef;

    @SerializedName("DiscountOnCustomer")
    private Double discountOnCustomer;

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

    @SerializedName("Items")
    private List<QuotationItemNew> items;
}
