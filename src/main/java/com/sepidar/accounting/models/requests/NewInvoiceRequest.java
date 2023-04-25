package com.sepidar.accounting.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sepidar.accounting.models.requests.helpers.NewInvoiceItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewInvoiceRequest {

    @JsonProperty("GUID")
    private String GUID;

    @JsonProperty("CurrencyRef")
    private Integer currencyRef;

    @JsonProperty("CustomerRef")
    private Integer customerRef;

    @JsonProperty("AddressRef")
    private Integer addressRef;

    @JsonProperty("SaleTypeRef")
    private Integer saleTypeRef;

    @JsonProperty("DiscountOnCustomer")
    private Double discountOnCustomer;

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

    @JsonProperty("Items")
    private List<NewInvoiceItem> newInvoiceItems;
}
