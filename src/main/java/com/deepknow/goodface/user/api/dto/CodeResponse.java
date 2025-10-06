package com.deepknow.goodface.user.api.dto;


import lombok.Data;

@Data
public class CodeResponse {
    private boolean success;
    private String message;
    private String code;
    private long expireAt;
    private long cooldownUntil;
}