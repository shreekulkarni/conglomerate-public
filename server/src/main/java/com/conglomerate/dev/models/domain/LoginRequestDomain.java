package com.conglomerate.dev.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class LoginRequestDomain {

    private String username;
    private String password;


    public LoginRequestDomain() {
        username = null;
        password = null;
    }
}
