package com.sepidar.accounting.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceResponse {

    @JsonProperty("InvoiceID")
    private Integer invoiceID;

    @JsonProperty("OrderRef")
    private Integer orderRef;

    @JsonProperty("QuotationRef")
    private Integer quotationRef;

    @JsonProperty("Number")
    private Integer number;

    @JsonProperty("Date")
    private String date;

    @JsonProperty("CustomerRef")
    private Integer customerRef;

    @JsonProperty("CurrencyRef")
    private Integer currencyRef;

    @JsonProperty("Rate")
    private Double rate;

    @JsonProperty("SaleTypeRef")
    private Integer saleTypeRef;

    @JsonProperty("AddressRef")
    private Integer addressRef;

    @JsonProperty("Price")
    private Integer price;

    @JsonProperty("Tax")
    private Integer tax;

    @JsonProperty("Duty")
    private Integer duty;

    @JsonProperty("Discount")
    private Integer discount;

    @JsonProperty("Addition")
    private Integer addition;

    @JsonProperty("NetPrice")
    private Long netPrice;

    @JsonProperty("InvoiceItems")
    private Integer invoiceItems;
}
