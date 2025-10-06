package com.deepknow.goodface.user.api.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class LoginByCodeRequest implements Serializable {
    private String phone;
    private String code;
}