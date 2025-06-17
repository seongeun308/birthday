package kim.birthday.service;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AuthException;
import kim.birthday.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenValidationService {

    private final JwtProvider jwtProvider;
    private final TokenStoreService tokenStoreService;

    public void validateAccessToken(String accessToken) {
        // 토큰 검증
        jwtProvider.validateToken(accessToken);
        // 블랙리스트 확인
        tokenStoreService.throwIfBlacklisted(accessToken);
    }

    public void validateRefreshToken(Long userId, String refreshToken) {
        // 토큰 검증
        jwtProvider.validateToken(refreshToken);

        // 요청에서 가져온 RT와 DB에서 가져온 RT 일치 여부 확인
        String savedToken = tokenStoreService.findRefreshToken(userId);
        if (!savedToken.equals(refreshToken))
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
    }
}
