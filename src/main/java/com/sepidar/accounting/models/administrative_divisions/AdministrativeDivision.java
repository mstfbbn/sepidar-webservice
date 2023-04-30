package com.sepidar.accounting.models.administrative_divisions;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministrativeDivision {

    @SerializedName("DivisionID")
    private Integer divisionId;

    @SerializedName("Title")
    private String title;

    @SerializedName("Type")
    private Integer type;

    @SerializedName("ParentDivisionRef")
    private Integer parentDivisionRef;
}
