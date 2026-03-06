package com.flashsale.user.entity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "otp")

public class Otp {

    @Id
    private String id;

    private  String email;

    private String phone;

    private String otp;

    private LocalDateTime expiredAt;
}
