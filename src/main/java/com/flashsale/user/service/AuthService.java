package com.flashsale.user.service;

import com.flashsale.auth.dto.request.LoginRequest;
import com.flashsale.auth.dto.request.RegisterRequest;
import com.flashsale.auth.dto.request.VerifyOtpRequest;
import com.flashsale.auth.dto.response.AuthResponse;
import com.flashsale.auth.dto.response.RegisterResponse;
import com.flashsale.security.JwtService;
import com.flashsale.user.entity.Otp;
import com.flashsale.user.entity.User;
import com.flashsale.user.repository.OtpRepository;
import com.flashsale.user.repository.UserRepository;

import com.flashsale.security.TokenBlackListService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private  final OtpRepository otpRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final TokenBlackListService tokenBlackListService;

    public RegisterResponse register(RegisterRequest request){

        String email = request.getEmail();
        String phone = request.getPhone();
        String password = request.getPassword();

        //check email exists
        if(userRepository.existsByEmail(email)){
            throw new RuntimeException("Email already exists");
        }

        //check phone exists
        if(userRepository.existsByPhone(phone)){
            throw new RuntimeException("Phone already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerified(false);
        user.setBalance(0.0);
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        generateOtp(email, phone);

        return new RegisterResponse(savedUser.getId());
    }

    private void generateOtp(String email, String phone){
        String otpCode = String.valueOf(
                new Random().nextInt(900000) + 100000
        );
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setPhone(phone);
        otp.setOtp(otpCode);
        otp.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);

        //demo: in ra console
        System.out.println("OTP:" + otpCode);
    }

    public void verifyOtp(VerifyOtpRequest request){
        String input = request.getInput();
        String otpCode= request.getOtp();

        Otp otp;

        if(input.contains("@")){
            otp = otpRepository
                    .findByEmailAndOtp(input, otpCode)
                    .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        }else{
            otp = otpRepository
                    .findByPhoneAndOtp(input, otpCode)
                    .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        }

        if(otp.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("OTP expired");
        }

        User user = userRepository
                .findByEmailOrPhone(input, input)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerified(true);

        userRepository.save(user);

        otpRepository.delete(otp);
    }



    public AuthResponse login(LoginRequest request){
        String input = request.getInput();
        String password = request.getPassword();

        User user = userRepository
                .findByEmailOrPhone(input, input)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Wrong password");
        }

        if(!user.isVerified()){
            throw new RuntimeException("User not verify");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken){

        if(!jwtService.validateToken(refreshToken, "refresh")){
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getRole()
        );
        String newRefreshToken = jwtService.generateRefreshToken(email);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String token) {
        // Do nothing (stateless JWT)
        if(token == null || token.isBlank()){
            throw new RuntimeException("Token is required");
        }

        if(!jwtService.validateToken(token, "access")){
            throw new RuntimeException("Invalid token");
        }

        long expirationMillis =
                jwtService.extractExpiration(token).getTime()
                        - System.currentTimeMillis();
        System.out.println("Exp time: " + jwtService.extractExpiration(token).getTime());
        System.out.println("Now: " + System.currentTimeMillis());
        System.out.println("Remaining: " + expirationMillis);

        tokenBlackListService.blacklistToken(token, expirationMillis);

    }
}
