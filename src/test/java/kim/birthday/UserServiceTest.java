package kim.birthday;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final static String VALID_EMAIL = "spring@email.com";
    private final static String VALID_PASSWORD = "spring123!";

    @Test
    void 회원가입_시_비밀번호_암호화를_수행한다() {
        SignupRequest request = new SignupRequest();
        request.setEmail(VALID_EMAIL);
        request.setPassword(VALID_PASSWORD);

        UserDto userDto = userService.signup(request);

        assertNotNull(userDto);
        assertEquals(request.getEmail(), userDto.getEmail());
        assertTrue(passwordEncoder.matches(request.getPassword(), userDto.getPassword()));
    }

    @Test
    void 이메일_중복_검사_성공() {
        회원가입_시_비밀번호_암호화를_수행한다();
        String email = "spring12@email.com";

        assertDoesNotThrow(() ->  userService.checkIfEmailExists(email));
    }

    @Test
    void 이메일_중복_검사_실패() {
        회원가입_시_비밀번호_암호화를_수행한다();

        AccountException accountException = assertThrows(AccountException.class, () -> userService.checkIfEmailExists(VALID_EMAIL));
        assertEquals(accountException.getMessage(), AccountErrorCode.EMAIL_IS_EXITS.getMessage());
    }

    @Test
    void 유효성_검사_시_빈값이_들어오면_실패() {
        SignupRequest request = new SignupRequest();
        request.setEmail("");
        request.setPassword("");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);


        List<String> expectedMessages = List.of(
                "이메일은 필수값입니다.",
                "비밀번호는 8~16자로 영문자, 숫자, 특수문자를 포함해야 합니다."
        );

        List<String> actualMessages = violations.stream()
                .map(v -> v.getMessage())
                .toList();

        assertFalse(violations.isEmpty());
        assertTrue(actualMessages.containsAll(expectedMessages));
    }

    @Test
    void 유효성_검사_시_옳지_않은_형식이_들어오면_실패() {
        SignupRequest request = new SignupRequest();
        request.setEmail("23");
        request.setPassword("123");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);


        List<String> expectedMessages = List.of(
                "올바른 이메일 형식이 아닙니다.",
                "비밀번호는 8~16자로 영문자, 숫자, 특수문자를 포함해야 합니다."
        );

        List<String> actualMessages = violations.stream()
                .map(v -> v.getMessage())
                .toList();

        assertFalse(violations.isEmpty());
        assertTrue(actualMessages.containsAll(expectedMessages));
    }
    
    @Test
    void 비밀번호_인증_성공() {
        회원가입_시_비밀번호_암호화를_수행한다();

        assertDoesNotThrow(() ->  userService.isMatchPassword(VALID_PASSWORD));
    }

    @Test
    void 비밀번호_인증_실패_시_예외를_던진다() {
        회원가입_시_비밀번호_암호화를_수행한다();

        AccountException e = assertThrows(AccountException.class, () -> userService.isMatchPassword(VALID_PASSWORD));
        assertEquals(AccountErrorCode.MISMATCH_PASSWORD, e.getErrorCode());
    }

}
