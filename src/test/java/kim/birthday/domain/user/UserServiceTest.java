package kim.birthday.domain.user;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private final UserService userService = new UserSerivceImpl();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void 회원가입_성공() {
        SignupRequest request = new SignupRequest();
        request.setEmail("spring@email.com");
        request.setPassword("spring123!");

        UserDto userDto = userService.signup(request);

        assertNotNull(userDto);
        assertEquals(request.getEmail(), userDto.getEmail());
        assertEquals(request.getPassword(), userDto.getPassword());
    }

    @Test
    void 이메일_중복_검사_성공() {
        회원가입_성공();
        String email = "spring12@email.com";

        assertDoesNotThrow(() ->  userService.checkifEmailExists(email));
    }

    @Test
    void 이메일_중복_검사_실패() {
        회원가입_성공();
        String email = "spring@email.com";

        assertThrows(EmailAlreadyExistsException.class, () -> userService.checkifEmailExists(email));
    }

    @Test
    void 유효성_검사_실패() {
        SignupRequest request = new SignupRequest();
        request.setEmail("");
        request.setPassword("");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);


        List<String> expectedMessages = List.of(
                "이메일은 필수값입니다.",
                "올바른 이메일 형식이 아닙니다.",
                "비밀번호는 필수값입니다.",
                "비밀번호는 8~16자로 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
        );

        List<String> actualMessages = violations.stream()
                .map(v -> v.getMessage())
                .toList();

        assertFalse(violations.isEmpty());
        assertTrue(actualMessages.containsAll(expectedMessages));
    }

    @Test
    void 비밀번호_해싱_매치_성공() {
        SignupRequest request = new SignupRequest();
        request.setEmail("spring@email.com");
        request.setPassword("spring123!");

        UserDto userDto = userService.signup(request);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean isMatch = passwordEncoder.matches(request.getPassword(), userDto.getPasword());

        assertTrue(isMatch);
    }

}
