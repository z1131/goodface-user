package com.deepknow.goodface.user.service.login;

import com.deepknow.goodface.user.repo.UserMapper;
import com.deepknow.goodface.user.repo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginService {
    @Autowired
    private CodeStore codeStore;

    @Autowired
    private UserMapper userMapper;

    public CodeStore.SendResult sendCode(String phone) {
        return codeStore.generate(phone);
    }

    public LoginResult loginByCode(String phone, String code) {
        CodeStore.VerifyResult v = codeStore.verify(phone, code);
        if (!v.success) {
            String msg = v.lockUntil > 0 ? ("已锁定至:" + v.lockUntil) : v.message;
            return LoginResult.fail(msg);
        }

        Instant now = Instant.now();
        Optional<User> existed = userMapper.findByPhone(phone);
        User user = existed.orElseGet(() -> {
            User u = new User();
            u.setPhone(phone);
            u.setUsername("用户" + tail(phone));
            u.setEmail("user" + tail(phone) + "@example.com");
            u.setMembership("普通用户");
            u.setBalance("0.00");
            u.setCreatedAt(now);
            u.setUpdatedAt(now);
            return u;
        });
        
        // 确保时间字段不为null
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(now);
        }
        user.setLastLoginAt(now);
        user.setUpdatedAt(now);
        
        userMapper.save(user);

        String token = "dev-token-" + UUID.randomUUID();
        return LoginResult.ok(token, user);
    }

    private String tail(String phone) {
        return phone == null ? "" : phone.substring(Math.max(0, phone.length() - 4));
    }

    public static class LoginResult {
        public final boolean success;
        public final String message;
        public final String token;
        public final User user;

        private LoginResult(boolean success, String message, String token, User user) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.user = user;
        }
        public static LoginResult ok(String token, User user) { return new LoginResult(true, null, token, user); }
        public static LoginResult fail(String msg) { return new LoginResult(false, msg, null, null); }
    }
}