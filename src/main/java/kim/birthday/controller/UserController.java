package kim.birthday.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kim.birthday.common.api.Api;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.TokenPair;
import kim.birthday.dto.request.ChangePasswordRequest;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.dto.response.LoginResponse;
import kim.birthday.dto.response.ReissueResponse;
import kim.birthday.service.UserService;
import kim.birthday.util.TokenUtils;
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

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

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
        TokenPair tokenPair = userService.login(user);

        LoginResponse loginResponse = new LoginResponse(tokenPair.getAccessToken());
        ResponseCookie refreshTokenCookie = TokenUtils.createHttpOnlyCookie(tokenPair.getRefreshToken().getToken(), days);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Api.ok(loginResponse));
    }


    @PostMapping("/account/password/verify")
    public ResponseEntity<Api<Void>> verifyPassword(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody String password
    ) {
        userService.verifyPassword(user.getUserId(), password);
        return ResponseEntity.ok(Api.ok());
    }

    @PatchMapping("/account/password")
    public ResponseEntity<Api<Void>> changePassword(
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

    @PostMapping("/reissue")
    public ResponseEntity<Api<ReissueResponse>> reissue(HttpServletRequest request) {
        String accessToken = TokenUtils.extractAccessToken(request);
        String refreshToken = TokenUtils.extractRefreshToken(request);

        TokenPair tokenPair = userService.reissueTokens(accessToken, refreshToken);

        ReissueResponse reissueResponse = new ReissueResponse(tokenPair.getAccessToken());
        ResponseCookie refreshTokenCookie = TokenUtils.createHttpOnlyCookie(tokenPair.getRefreshToken().getToken(), days);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(Api.ok(reissueResponse));
    }
}
