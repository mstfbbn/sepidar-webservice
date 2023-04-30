package com.sepidar.accounting.models.property;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Property {

    @SerializedName("PropertyID")
    private Integer propertyId;

    @SerializedName("Title")
    private String title;
}
