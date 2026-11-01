package com.example.hospitalManagement.dto;

import lombok.Data;


/**
 * When a client attempts to log in, they send a POST request to the /auth/login endpoint. This request includes a JSON body (20:56) containing:
 * <p>
 * username: The user's registered username.
 * <p>
 * password: The user's plain-text password.
 */
@Data
public class LoginRequestDto    {

    private String username;
    private String password;
}
