package com.sepidar.accounting.models.unit;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Unit {

    @SerializedName("UnitID")
    private Integer unitId;

    @SerializedName("Title")
    private String title;
}
