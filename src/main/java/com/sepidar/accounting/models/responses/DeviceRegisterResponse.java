package com.sepidar.accounting.models.responses;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRegisterResponse {

    @SerializedName("Cypher")
    private String cypher;

    @SerializedName("IV")
    private String iv;

    @SerializedName("DeviceTitle")
    private String deviceTitle;
}
