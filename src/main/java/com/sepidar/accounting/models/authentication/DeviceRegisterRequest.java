package com.sepidar.accounting.models.authentication;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class DeviceRegisterRequest {

    @SerializedName("Cypher")
    private String cypher;

    @SerializedName("IV")
    private String iv;

    @SerializedName("IntegrationID")
    private String integrationId;
}
