package com.sepidar.accounting.models.invoice;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Invoice {

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
    private Double price;

    @SerializedName("Tax")
    private Double tax;

    @SerializedName("Duty")
    private Double duty;

    @SerializedName("Discount")
    private Double discount;

    @SerializedName("Addition")
    private Double addition;

    @SerializedName("NetPrice")
    private Double netPrice;

    @SerializedName("InvoiceItems")
    private List<InvoiceItem> invoiceItems;
}
