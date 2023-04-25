package com.sepidar.accounting.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    @JsonProperty("Token")
    private String token;

    @JsonProperty("UserID")
    private Integer userId;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("CanEditCustomer")
    private Boolean canEditCustomer;

    @JsonProperty("CanRegisterCustomer")
    private Boolean canRegisterCustomer;

    @JsonProperty("CanRegisterOrder")
    private Boolean canRegisterOrder;

    @JsonProperty("CanRegisterReturnOrder")
    private Boolean canRegisterReturnOrder;

    @JsonProperty("CanRegisterInvoice")
    private Boolean canRegisterInvoice;

    @JsonProperty("CanRegisterReturnInvoice")
    private Boolean canRegisterReturnInvoice;

    @JsonProperty("CanPrintInvoice")
    private Boolean canPrintInvoice;

    @JsonProperty("CanPrintReturnInvoice")
    private Boolean canPrintReturnInvoice;

    @JsonProperty("CanPrintInvoiceBeforeSend")
    private Boolean canPrintInvoiceBeforeSend;

    @JsonProperty("CanPrintReturnInvoiceBeforeSend")
    private Boolean canPrintReturnInvoiceBeforeSend;

    @JsonProperty("CanRevokeInvoice")
    private Boolean canRevokeInvoice;
}
