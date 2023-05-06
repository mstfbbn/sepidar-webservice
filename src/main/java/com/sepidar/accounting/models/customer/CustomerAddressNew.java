package com.sepidar.accounting.models.customer;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CustomerAddressNew {

    @SerializedName("GUID")
    protected String guid;

    @SerializedName("Title")
    protected String title;

    @SerializedName("IsMain")
    protected Boolean isMain;

    @SerializedName("CityRef")
    protected Integer cityRef;

    @SerializedName("Address")
    protected String address;

    @SerializedName("ZipCode")
    protected String zipCode;

    @SerializedName("Latitude")
    protected Double latitude;

    @SerializedName("Longitude")
    protected Double longitude;
}
