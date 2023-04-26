package com.sepidar.accounting.models.responses;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerationVersionResponse {

    @SerializedName("GenerationVersion")
    private String generationVersion;

    @SerializedName("LockNumber")
    private String lockNumber;
}
