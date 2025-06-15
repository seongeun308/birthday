package kim.birthday.service;

import kim.birthday.domain.RefreshToken;
import kim.birthday.dto.TokenDto;
import kim.birthday.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void add(long userId, TokenDto refreshTokenDto) {
        RefreshToken refreshToken = new RefreshToken(userId, refreshTokenDto.getToken(), refreshTokenDto.getExpiresAt());
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token saved {}", refreshToken);
    }
}
