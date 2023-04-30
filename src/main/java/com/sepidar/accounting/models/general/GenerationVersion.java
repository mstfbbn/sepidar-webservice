package com.sepidar.accounting.models.general;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerationVersion {

    @SerializedName("GenerationVersion")
    private String generationVersion;

    @SerializedName("LockNumber")
    private String lockNumber;
}
