package com.deepknow.goodface.user.api.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class LoginByCodeRequest implements Serializable {
    private static final long serialVersionUID = 7760906363837447772L;
    private String phone;
    private String code;
}