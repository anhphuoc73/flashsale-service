package com.flashsale.user.repository;

import com.flashsale.user.entity.Otp;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpRepository extends MongoRepository<Otp, String> {

    Optional<Otp> findByEmailAndOtp(String email, String otp);

    Optional<Otp> findByPhoneAndOtp(String phone, String otp);
}