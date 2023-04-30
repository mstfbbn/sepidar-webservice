package com.sepidar.accounting.models.invoice;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponse {

    @SerializedName("InvoiceID")
    private Integer invoiceID;

    @SerializedName("OrderRef")
    private Integer orderRef;

    @SerializedName("QuotationRef")
    private Integer quotationRef;

    @SerializedName("Number")
    private Integer number;

    @SerializedName("Date")
    private String date;

    @SerializedName("CustomerRef")
    private Integer customerRef;

    @SerializedName("CurrencyRef")
    private Integer currencyRef;

    @SerializedName("Rate")
    private Double rate;

    @SerializedName("SaleTypeRef")
    private Integer saleTypeRef;

    @SerializedName("AddressRef")
    private Integer addressRef;

    @SerializedName("Price")
    private Integer price;

    @SerializedName("Tax")
    private Integer tax;

    @SerializedName("Duty")
    private Integer duty;

    @SerializedName("Discount")
    private Integer discount;

    @SerializedName("Addition")
    private Integer addition;

    @SerializedName("NetPrice")
    private Long netPrice;

    @SerializedName("InvoiceItems")
    private Integer invoiceItems;
}
