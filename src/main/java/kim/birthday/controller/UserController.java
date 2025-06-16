package kim.birthday.controller;

import jakarta.validation.Valid;
import kim.birthday.common.api.Api;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.TokenDto;
import kim.birthday.dto.TokenPair;
import kim.birthday.dto.request.ChangePasswordRequest;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.dto.response.LoginResponse;
import kim.birthday.service.TokenService;
import kim.birthday.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    @Value("${jwt.expiration-days}")
    private int days;

    @PostMapping("/signup")
    public ResponseEntity<Api<Void>> signup(@Valid @RequestBody SignupRequest request) {
        userService.checkIfEmailExists(request.getEmail());
        userService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Api.created());
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginResponse>> login(@AuthenticationPrincipal AuthenticatedUser user) {
        TokenPair tokenPair = tokenService.issueTokens(user);

        TokenDto accessToken = tokenPair.getAccessToken();
        TokenDto refreshToken = tokenPair.getRefreshToken();

        LoginResponse loginResponse = new LoginResponse(accessToken.getToken(), accessToken.getExpiresAt());
        ResponseCookie refreshTokenCookie = createHttpOnlyCookie(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Api.ok(loginResponse));
    }

    private ResponseCookie createHttpOnlyCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)  // Todo : 배포 시 .secure(true)로 변경
                .sameSite("Strict")
                .path("/reissue")
                .maxAge(Duration.ofDays(days))
                .build();
    }

    @PostMapping("/account/password/verify")
    public ResponseEntity<Api<Void>> verifyPassword (
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody String password
    ) {
        userService.verifyPassword(user.getUserId(), password);
        return ResponseEntity.ok(Api.ok());
    }

    @PatchMapping("/account/password")
    public ResponseEntity<Api<Void>> changePassword (
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        ensurePasswordsMatch(user.getPublicId(), request);
        userService.changePassword(user, request);
        return ResponseEntity.ok(Api.ok());
    }

    private void ensurePasswordsMatch(String publicId, ChangePasswordRequest request) {
        if (!request.isPasswordMatch()) {
            log.warn("[비밀번호변경] 사용자 [{}] 실패 - 비밀번호 불일치", publicId);
            throw new AccountException(AccountErrorCode.PASSWORD_MISMATCH);
        }
    }
}
