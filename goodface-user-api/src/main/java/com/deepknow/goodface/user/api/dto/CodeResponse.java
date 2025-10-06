package com.deepknow.goodface.user.api.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class CodeResponse implements Serializable {
    private static final long serialVersionUID = 2285908363809925696L;
    private boolean success;
    private String message;
    private String code;
    private long expireAt;
    private long cooldownUntil;
}