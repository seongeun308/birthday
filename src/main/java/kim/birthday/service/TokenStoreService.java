package kim.birthday.service;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.error.TokenErrorCode;
import kim.birthday.common.exception.AuthException;
import kim.birthday.common.exception.TokenException;
import kim.birthday.domain.RefreshToken;
import kim.birthday.store.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenStoreService {

    private final RedisTemplate<String, String> accessTokenBlackList;
    private final RefreshTokenStore refreshTokenStore;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void storeRefreshToken(Long userId, String refreshToken, LocalDateTime expiresAt) {
        RefreshToken token = new RefreshToken(userId, refreshToken, expiresAt);
        refreshTokenStore.save(token);
    }

    public String findRefreshToken(Long userId) {
        RefreshToken refreshToken = refreshTokenStore.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        if (LocalDateTime.now().isAfter(refreshToken.getExpiresAt()))
            throw new TokenException(TokenErrorCode.EXPIRED);

        return refreshToken.getRefreshToken();
    }

    public void deleteRefreshToken(Long userId) {
        refreshTokenStore.deleteById(userId);
    }

    public void blacklistAccessToken(String accessToken, Date expiration) {
        long now = System.currentTimeMillis();
        long expireAt = expiration.getTime();
        long ttlMillis = expireAt - now;

        if (ttlMillis > 0) {
            String key = BLACKLIST_PREFIX + accessToken;
            accessTokenBlackList.opsForValue().set(key, "true", Duration.ofMillis(ttlMillis));
        }
    }

    public void throwIfBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        if (accessTokenBlackList.hasKey(key))
            throw new AuthException(AuthErrorCode.BLACKLISTED_TOKEN);
    }
}
