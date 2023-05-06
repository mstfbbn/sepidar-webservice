package com.sepidar.accounting.models.customer;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
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

