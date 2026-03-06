package com.flashsale.user.entity;

import com.flashsale.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
@Document(collection = "users")

public class User extends BaseEntity {

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String phone;

    private String password;

    private double balance;

    private boolean verified;

    private String role;
}