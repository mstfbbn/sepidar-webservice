package com.sepidar.accounting.models.invoice;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class InvoiceNewOnQuotation {

    @SerializedName("QuatationID")
    private Integer quotationId;
}
