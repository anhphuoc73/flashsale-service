package com.flashsale.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Email or phone is required")
    private String input;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{4,6}$", message = "OTP must be 4-6 digits")
    private String otp;
}
