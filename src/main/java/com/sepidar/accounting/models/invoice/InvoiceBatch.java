package com.sepidar.accounting.models.invoice;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceBatch {

    @SerializedName("GUID")
    private String GUID;

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

    @SerializedName("Items")
    private List<InvoiceItemNew> newInvoiceItems;
}
