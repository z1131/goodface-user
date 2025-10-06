package com.deepknow.goodface.user.repo.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class User {
    private Long id;
    private String phone;
    private String username;
    private String email;
    private String membership;
    private String balance;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;
}