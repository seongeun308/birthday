package kim.birthday.service;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AuthException;
import kim.birthday.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void blacklistAccessToken(String accessToken) {
        long now = System.currentTimeMillis();
        long expireAt = jwtProvider.getPayload(accessToken).getExpiration().getTime();
        long ttlMillis = expireAt - now;

        if (ttlMillis > 0) {
            String key = BLACKLIST_PREFIX + accessToken;
            redisTemplate.opsForValue().set(key, "true", Duration.ofMillis(ttlMillis));
        }
    }

    public void throwIfBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        if (redisTemplate.hasKey(key))
            throw new AuthException(AuthErrorCode.BLACKLISTED_TOKEN);
    }
}