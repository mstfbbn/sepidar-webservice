package com.sepidar.accounting.models.customer;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerEdit {

    @SerializedName("CustomerID")
    private String customerId;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

    @SerializedName("BirthDate")
    private String birthDate;

    @SerializedName("NationalID")
    private String nationalId;

    @SerializedName("EconomicCode")
    private String economicCode;

    @SerializedName("Version")
    private Integer version;

    @SerializedName("Addresses")
    private List<CustomerAddress> addresses;
}

