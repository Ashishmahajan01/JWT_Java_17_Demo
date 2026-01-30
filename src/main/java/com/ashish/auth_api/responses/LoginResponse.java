package com.ashish.auth_api.responses;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginResponse {
    @Getter
    private String token;

    private long expiresIn;

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this; // For method chaining (builder pattern)
    }

}
