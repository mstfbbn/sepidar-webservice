package com.sepidar.accounting.models.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class RsaRawPublicKey {

    private byte[] modulus;

    private byte[] exponent;
}
