package com.sepidar.accounting.models.item;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tracing {

    @SerializedName("TracingID")
    private Integer tracingId;

    @SerializedName("Title")
    private String title;

    @SerializedName("IsSelectable")
    private Boolean isSelectable;
}
