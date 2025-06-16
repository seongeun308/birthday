package kim.birthday.store;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Transactional
public class PasswordVerificationStore {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "password_verified:";
    private static final Duration TTL = Duration.ofMinutes(3);
    private static final String TRUE = "true";

    public void markVerified(Long userId) {
        String key = KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, TRUE, TTL);
    }

    public void verifyOrThrow(Long userId) {
        String key = KEY_PREFIX + userId;
        String value = redisTemplate.opsForValue().get(key);
        if (!TRUE.equals(value))
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_VERIFIED);
    }

    public void clear(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}