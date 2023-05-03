package com.sepidar.accounting.models.quotation;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Quotation {

    @SerializedName("ID")
    private Integer id;

    @SerializedName("GUID")
    private String guid;

    @SerializedName("Number")
    private Integer number;

    @SerializedName("Date")
    private Date date;

    @SerializedName("CurrencyRef")
    private Integer currencyRef;

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

    @SerializedName("NetPrice")
    private Double netPrice;

    @SerializedName("Items")
    private List<QuotationItem> items;
}
