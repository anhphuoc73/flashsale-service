package com.flashsale.auth.controller;

import com.flashsale.auth.dto.request.LoginRequest;
import com.flashsale.auth.dto.request.RefreshRequest;
import com.flashsale.auth.dto.request.RegisterRequest;
import com.flashsale.auth.dto.request.VerifyOtpRequest;
import com.flashsale.auth.dto.response.AuthResponse;
import com.flashsale.auth.dto.response.RegisterResponse;
import com.flashsale.user.service.AuthService;
import com.flashsale.exception.ApiResponse;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>>  register(
        @Valid @RequestBody RegisterRequest request
    ){
        RegisterResponse response = authService.register(request);
        return ApiResponse.created(
                "Register success. Please verify OTP",
                response
        );
    }

    //VERIFY OTP
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verify(
            @Valid @RequestBody VerifyOtpRequest request
    ){
        authService.verifyOtp(request);
        return ApiResponse.ok("Verify success", null);
    }

    //LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    //REFRESH
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(
            authService.refreshToken(request.getRefreshToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @RequestHeader("Authorization") String authHeader
    ){

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);


        authService.logout(token);

        return ApiResponse.ok("logout success", null);
    }
}