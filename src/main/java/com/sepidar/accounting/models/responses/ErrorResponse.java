package com.sepidar.accounting.models.responses;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    @SerializedName("Type")
    private Integer type;

    @SerializedName("Message")
    private String message;
}
