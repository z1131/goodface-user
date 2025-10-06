package com.deepknow.goodface.user.service.login;

import com.deepknow.goodface.user.api.UserAuthService;
import com.deepknow.goodface.user.api.dto.CodeResponse;
import com.deepknow.goodface.user.api.dto.LoginByCodeRequest;
import com.deepknow.goodface.user.api.dto.LoginResponse;
import com.deepknow.goodface.user.repo.entity.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private LoginService loginService;

    @Override
    public CodeResponse sendSmsCode(String phone) {
        CodeStore.SendResult r = loginService.sendCode(phone);
        CodeResponse resp = new CodeResponse();
        resp.setSuccess(r.success);
        resp.setMessage(r.message);
        resp.setCode(r.code); // 开发态返回验证码
        resp.setExpireAt(r.expireAt);
        resp.setCooldownUntil(r.cooldownUntil);
        return resp;
    }

    @Override
    public LoginResponse loginByCode(LoginByCodeRequest request) {
        LoginService.LoginResult r = loginService.loginByCode(request.getPhone(), request.getCode());
        LoginResponse resp = new LoginResponse();
        if (!r.success) {
            resp.setSuccess(false);
            resp.setMessage(r.message);
            return resp;
        }
        User u = r.user;
        resp.setSuccess(true);
        resp.setToken(r.token);
        resp.setUsername(u.getUsername());
        resp.setEmail(u.getEmail());
        resp.setMembership(u.getMembership());
        resp.setBalance(u.getBalance());
        return resp;
    }
}