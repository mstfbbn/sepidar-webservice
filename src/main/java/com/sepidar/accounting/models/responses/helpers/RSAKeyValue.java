package com.sepidar.accounting.models.responses.helpers;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "RSAKeyValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class RSAKeyValue {

    @XmlElement(name = "Modulus")
    private String modulus;

    @XmlElement(name = "Exponent")
    private String exponent;
}
