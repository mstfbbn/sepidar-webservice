package com.sepidar.accounting.models.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SepidarRequestHeader {

    private String generationVersion = "101";
    private String integrationId;
    private String arbitraryCode;
    private String arbitraryCodeEncoded;
    private String token;
}
