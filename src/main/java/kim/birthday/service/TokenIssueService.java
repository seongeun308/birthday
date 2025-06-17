package kim.birthday.service;

import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.TokenDto;
import kim.birthday.dto.TokenPair;
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

    public TokenPair issueTokens(AuthenticatedUser user) {
        TokenDto accessTokenDto = jwtProvider.generateAccessToken(
                user.getPublicId(),
                Map.of("role", user.getRole())
        );
        TokenDto refreshTokenDto = jwtProvider.generateRefreshToken();

        return new TokenPair(accessTokenDto, refreshTokenDto);
    }
}
