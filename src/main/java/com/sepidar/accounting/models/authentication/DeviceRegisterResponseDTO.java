package com.sepidar.accounting.models.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class DeviceRegisterResponseDTO {

    private String xmlString;
    private String deviceTitle;
}
