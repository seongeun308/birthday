package kim.birthday.controller;

import jakarta.validation.Valid;
import kim.birthday.common.api.Api;
import kim.birthday.domain.Account;
import kim.birthday.dto.TokenDto;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.dto.response.LoginResponse;
import kim.birthday.service.RefreshTokenService;
import kim.birthday.service.UserService;
import kim.birthday.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    @Value("${jwt.expiration-days}")
    private int days;

    @PostMapping("/signup")
    public ResponseEntity<Api<Void>> signup(@Valid @RequestBody SignupRequest request) {
        userService.checkIfEmailExists(request.getEmail());
        userService.signup(request);

        HttpStatus status = HttpStatus.CREATED;
        Api<Void> api = Api.success(status, null);

        return ResponseEntity.status(status)
                .body(api);
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginResponse>> login(@AuthenticationPrincipal Account account) {
        TokenDto accessTokenDto = jwtProvider.generateAccessToken(account.getPublicId(), Map.of("role", account.getRole()));
        TokenDto refreshTokenDto = jwtProvider.generateRefreshToken();

        refreshTokenService.add(account.getId(), refreshTokenDto);

        LoginResponse loginResponse = new LoginResponse(accessTokenDto.getToken(), accessTokenDto.getExpiresAt());
        Api<LoginResponse> api = Api.success(loginResponse);
        ResponseCookie refreshTokenCookie = createHttpOnlyCookie(refreshTokenDto.getToken());

        return ResponseEntity
                .status(api.getStatusCode())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(api);
    }

    private ResponseCookie createHttpOnlyCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                // Todo : 배포 시.secure(true)로 변경
                .secure(false)
                .sameSite("Strict")
                .path("/refresh")
                .maxAge(Duration.ofDays(days))
                .build();
    }
}
