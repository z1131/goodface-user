package com.deepknow.goodface.user.service.login;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CodeStore {
    private final SecureRandom random = new SecureRandom();

    private static class CodeEntry {
        String code;
        long expireAt;
    }

    private static class AttemptEntry {
        int count;
        long lockUntil;
    }

    private final Map<String, CodeEntry> codes = new ConcurrentHashMap<>();
    private final Map<String, AttemptEntry> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    private static final long CODE_TTL_MS = 5 * 60 * 1000L;
    private static final long LOCK_TTL_MS = 15 * 60 * 1000L;
    private static final long SEND_COOLDOWN_MS = 60 * 1000L;

    public synchronized SendResult generate(String phone) {
        long now = System.currentTimeMillis();
        Long cd = cooldowns.get(phone);
        if (cd != null && cd > now) {
            return SendResult.cooldown(cd);
        }
        String code = String.format("%06d", random.nextInt(1_000_000));
        CodeEntry entry = new CodeEntry();
        entry.code = code;
        entry.expireAt = now + CODE_TTL_MS;
        codes.put(phone, entry);
        cooldowns.put(phone, now + SEND_COOLDOWN_MS);
        attempts.remove(phone);
        return SendResult.ok(code, entry.expireAt, cooldowns.get(phone));
    }

    public VerifyResult verify(String phone, String code) {
        long now = System.currentTimeMillis();
        AttemptEntry att = attempts.computeIfAbsent(phone, k -> new AttemptEntry());
        if (att.lockUntil > now) {
            return VerifyResult.locked(att.lockUntil);
        }
        CodeEntry entry = codes.get(phone);
        if (entry == null || entry.expireAt < now) {
            return VerifyResult.fail("验证码已过期或未发送");
        }
        if (entry.code.equals(code)) {
            codes.remove(phone);
            attempts.remove(phone);
            return VerifyResult.ok();
        }
        att.count += 1;
        if (att.count >= 3) {
            att.lockUntil = now + LOCK_TTL_MS;
            return VerifyResult.locked(att.lockUntil);
        }
        return VerifyResult.fail("验证码错误（" + att.count + "/3）");
    }

    public static class SendResult {
        public final boolean success;
        public final String code;
        public final long expireAt;
        public final long cooldownUntil;
        public final String message;

        private SendResult(boolean success, String code, long expireAt, long cooldownUntil, String message) {
            this.success = success;
            this.code = code;
            this.expireAt = expireAt;
            this.cooldownUntil = cooldownUntil;
            this.message = message;
        }
        public static SendResult ok(String code, long expireAt, long cooldownUntil) {
            return new SendResult(true, code, expireAt, cooldownUntil, null);
        }
        public static SendResult cooldown(long cooldownUntil) {
            return new SendResult(false, null, 0L, cooldownUntil, "发送过于频繁，请稍后再试");
        }
    }

    public static class VerifyResult {
        public final boolean success;
        public final String message;
        public final long lockUntil;

        private VerifyResult(boolean success, String message, long lockUntil) {
            this.success = success;
            this.message = message;
            this.lockUntil = lockUntil;
        }
        public static VerifyResult ok() { return new VerifyResult(true, null, 0L); }
        public static VerifyResult fail(String msg) { return new VerifyResult(false, msg, 0L); }
        public static VerifyResult locked(long until) { return new VerifyResult(false, "错误次数过多，已锁定", until); }
    }
}