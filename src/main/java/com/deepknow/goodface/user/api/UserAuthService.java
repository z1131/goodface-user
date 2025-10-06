package com.deepknow.goodface.user.api;


import com.deepknow.goodface.user.api.dto.CodeResponse;
import com.deepknow.goodface.user.api.dto.LoginByCodeRequest;
import com.deepknow.goodface.user.api.dto.LoginResponse;

public interface UserAuthService {
    CodeResponse sendSmsCode(String phone);
    LoginResponse loginByCode(LoginByCodeRequest request);
}