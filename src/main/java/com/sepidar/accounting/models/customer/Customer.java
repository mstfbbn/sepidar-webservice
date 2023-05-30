package com.sepidar.accounting.models.customer;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Customer {

    @SerializedName("CustomerID")
    private Integer customerId;

    @SerializedName("GUID")
    private String guid;

    @SerializedName("Title")
    private String title;

    @SerializedName("Code")
    private String code;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

    @SerializedName("Remainder")
    private Double remainder;

    @SerializedName("CreditRemainder")
    private Double creditRemainder;

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

    @SerializedName("Version")
    private Integer version;

    @SerializedName("GroupingRef")
    private Integer groupingRef;

    @SerializedName("DiscountRate")
    private Double discountRate;

    @SerializedName("Addresses")
    private List<CustomerAddress> addresses;
}
