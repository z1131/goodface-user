package com.deepknow.goodface.user.api.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class LoginResponse implements Serializable {
    private boolean success;
    private String message;
    private String token;
    private String username;
    private String email;
    private String membership;
    private String balance;

}