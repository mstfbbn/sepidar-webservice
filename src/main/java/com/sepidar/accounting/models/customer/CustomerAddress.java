package com.sepidar.accounting.models.customer;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerAddress extends CustomerAddressNew {

    @SerializedName("CustomerAddressID")
    private Integer customerAddressId;
}
