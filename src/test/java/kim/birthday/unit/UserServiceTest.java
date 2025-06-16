package kim.birthday.unit;

import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.common.exception.AuthException;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.ChangePasswordRequest;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.security.token.JwtAuthenticationToken;
import kim.birthday.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SignupRequest signupRequest;
    private AuthenticatedUser user;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        if (testInfo.getTags().contains("without-signup")) return;

        UserDto userDto = userService.signup(signupRequest);
        user = new AuthenticatedUser(userDto.getUserId(), userDto.getPublicId(), userDto.getRole());

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(user, List.of(user.getRole().toAuthority()));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public UserServiceTest() {
        SignupRequest request = new SignupRequest();
        request.setEmail("spring@spring.com");
        request.setPassword("spring12!");
        this.signupRequest = request;
    }

    @Tag("without-signup")
    @Test
    void 회원가입_시_비밀번호_암호화를_수행한다() {
        UserDto userDto = userService.signup(signupRequest);

        assertNotNull(userDto);
        assertEquals(signupRequest.getEmail(), userDto.getEmail());
        assertTrue(passwordEncoder.matches(signupRequest.getPassword(), userDto.getPassword()));
    }

    @Tag("without-signup")
    @Test
    void 이메일_중복_검사_성공() {
        assertDoesNotThrow(() -> userService.checkIfEmailExists(signupRequest.getEmail()));
    }

    @Test
    void 이메일_중복_검사_실패() {
        AccountException accountException = assertThrows(AccountException.class, () ->
                userService.checkIfEmailExists(signupRequest.getEmail())
        );
        assertEquals(accountException.getMessage(), AccountErrorCode.EMAIL_IS_EXITS.getMessage());
    }

    @Test
    void 비밀번호_인증_성공() {
        assertDoesNotThrow(() -> userService.verifyPassword(user.getUserId(), signupRequest.getPassword()));
    }

    @Test
    void 비밀번호_인증_실패_시_예외를_던진다() {
        AuthException e = assertThrows(AuthException.class, () ->
                userService.verifyPassword(user.getUserId(), "qwer14234!")
        );
        assertEquals(AuthErrorCode.MISMATCH_PASSWORD, e.getErrorCode());
    }

    @Test
    void 비밀번호_인증_수행_후_비밀번호_변경_시_성공() {
        userService.verifyPassword(user.getUserId(), signupRequest.getPassword());
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                "change123!",
                "change123!"
        );
        assertDoesNotThrow(() -> userService.changePassword(user, changePasswordRequest));
    }

    @Test
    void 비밀번호_인증_수행_전_비밀번호_변경_시_실패() {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "change123!",
                "change123"
        );

        AuthException e = assertThrows(AuthException.class, () -> userService.changePassword(user, request));
        assertEquals(AuthErrorCode.PASSWORD_NOT_VERIFIED, e.getErrorCode());
    }
}
