package kim.birthday;

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


import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입_시_비밀번호_암호화를_수행한다() {
        SignupRequest request = new SignupRequest();
        request.setEmail("spring@email.com");
        request.setPassword("spring123!");

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
        String email = "spring@email.com";

        AccountException accountException = assertThrows(AccountException.class, () -> userService.checkIfEmailExists(email));
        assertEquals(accountException.getMessage(), AccountErrorCode.EMAIL_IS_EXITS.getMessage());
    }
//
//    @Test
//    void 유효성_검사_실패() {
//        SignupRequest request = new SignupRequest();
//        request.setEmail("");
//        request.setPassword("");
//
//        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
//        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
//
//
//        List<String> expectedMessages = List.of(
//                "이메일은 필수값입니다.",
//                "올바른 이메일 형식이 아닙니다.",
//                "비밀번호는 필수값입니다.",
//                "비밀번호는 8~16자로 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
//        );
//
//        List<String> actualMessages = violations.stream()
//                .map(v -> v.getMessage())
//                .toList();
//
//        assertFalse(violations.isEmpty());
//        assertTrue(actualMessages.containsAll(expectedMessages));
//    }
}
