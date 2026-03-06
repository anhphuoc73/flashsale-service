package com.flashsale.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email or phone is required")
    private String input;

    @NotBlank(message = "Password is required")
    private String password;
}