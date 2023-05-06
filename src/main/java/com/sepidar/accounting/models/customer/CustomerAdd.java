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
public class CustomerAdd {

    @SerializedName("GUID")
    private String guid;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

    @SerializedName("CustomerType")
    private Integer customerType;

    @SerializedName("Name")
    private String name;

    @SerializedName("LastName")
    private String lastName;

    @SerializedName("BirthDate")
    private String birthDate;

    @SerializedName("NationalID")
    private String nationalId;

    @SerializedName("EconomicCode")
    private String economicCode;

    @SerializedName("Addresses")
    private List<CustomerAddressNew> addresses;
}
