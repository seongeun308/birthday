package kim.birthday.service;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AuthException;
import kim.birthday.domain.RefreshToken;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.TokenDto;
import kim.birthday.dto.TokenPair;
import kim.birthday.store.RefreshTokenStore;
import kim.birthday.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenIssueService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    public TokenPair issueTokens(AuthenticatedUser user) {
        TokenDto accessTokenDto = jwtProvider.generateAccessToken(user.getPublicId(), Map.of("role", user.getRole()));
        TokenDto refreshTokenDto = jwtProvider.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken(user.getUserId(), refreshTokenDto.getToken());
        refreshTokenStore.save(refreshToken);

        return new TokenPair(accessTokenDto, refreshTokenDto);
    }

    public TokenPair reissueTokens(AuthenticatedUser user, String token) {
        RefreshToken refreshToken = refreshTokenStore.findById(user.getUserId())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        if (!refreshToken.getRefreshToken().equals(token))
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);

        refreshTokenStore.deleteById(user.getUserId());

        return issueTokens(user);
    }
}
