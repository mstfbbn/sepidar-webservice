package com.sepidar.accounting.models.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SepidarConfiguration {

    private String apiVersion = "101";
    private String url = "http://localhost:7373";
    private String deviceId = "1000aaaa";
}
