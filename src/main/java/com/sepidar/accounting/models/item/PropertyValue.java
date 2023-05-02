package com.sepidar.accounting.models.item;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyValue {

    @SerializedName("PropertyRef")
    private Integer propertyRef;

    @SerializedName("Value")
    private String value;
}
