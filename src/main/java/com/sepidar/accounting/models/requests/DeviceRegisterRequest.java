package com.sepidar.accounting.models.requests;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRegisterRequest {

    @SerializedName("Cypher")
    private String cypher;

    @SerializedName("IV")
    private String iv;

    @SerializedName("IntegrationID")
    private String integrationId;
}
