package com.sepidar.accounting.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("UserName")
    private String username;

    @JsonProperty("PasswordHash")
    private String md5HashedPassword;
}
