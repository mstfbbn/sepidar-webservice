package com.sepidar.accounting.models.authentication;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @SerializedName("UserName")
    private String username;

    @SerializedName("PasswordHash")
    private String md5HashedPassword;
}
