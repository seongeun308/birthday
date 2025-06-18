package kim.birthday.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kim.birthday.common.error.TokenErrorCode;
import kim.birthday.common.exception.TokenException;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;

public class TokenUtils {

    private static final String HEADER_STRING = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String SAME_SITE = "Strict";
    private static final String PATH = "/reissue";

    public static String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(PREFIX))
            throw new TokenException(TokenErrorCode.MISSING_TOKEN);

        return header.substring(PREFIX.length());
    }

    public static String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            throw new TokenException(TokenErrorCode.MISSING_TOKEN);

        return getRefreshTokenFromCookie(cookies);
    }

    private static String getRefreshTokenFromCookie(Cookie[] cookies) {
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .orElseThrow(() -> new TokenException(TokenErrorCode.MISSING_TOKEN))
                .getValue();
    }

    public static ResponseCookie createHttpOnlyCookie(String refreshToken, int days) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)  // Todo : 배포 시 .secure(true)로 변경
                .sameSite(SAME_SITE)
                .path(PATH)
                .maxAge(Duration.ofDays(days))
                .build();
    }

    public static ResponseCookie createExpiredCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)  // Todo : 배포 시 .secure(true)로 변경
                .sameSite(SAME_SITE)
                .path(PATH)
                .maxAge(0)
                .build();
    }
}