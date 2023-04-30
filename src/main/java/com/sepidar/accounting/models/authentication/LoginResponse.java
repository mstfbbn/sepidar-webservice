package com.sepidar.accounting.models.authentication;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    @SerializedName("Token")
    private String token;

    @SerializedName("UserID")
    private Integer userId;

    @SerializedName("Title")
    private String title;

    @SerializedName("CanEditCustomer")
    private Boolean canEditCustomer;

    @SerializedName("CanRegisterCustomer")
    private Boolean canRegisterCustomer;

    @SerializedName("CanRegisterOrder")
    private Boolean canRegisterOrder;

    @SerializedName("CanRegisterReturnOrder")
    private Boolean canRegisterReturnOrder;

    @SerializedName("CanRegisterInvoice")
    private Boolean canRegisterInvoice;

    @SerializedName("CanRegisterReturnInvoice")
    private Boolean canRegisterReturnInvoice;

    @SerializedName("CanPrintInvoice")
    private Boolean canPrintInvoice;

    @SerializedName("CanPrintReturnInvoice")
    private Boolean canPrintReturnInvoice;

    @SerializedName("CanPrintInvoiceBeforeSend")
    private Boolean canPrintInvoiceBeforeSend;

    @SerializedName("CanPrintReturnInvoiceBeforeSend")
    private Boolean canPrintReturnInvoiceBeforeSend;

    @SerializedName("CanRevokeInvoice")
    private Boolean canRevokeInvoice;
}
