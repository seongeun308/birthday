package kim.birthday;

import com.fasterxml.jackson.databind.ObjectMapper;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.ChangePasswordRequest;
import kim.birthday.dto.request.LoginRequest;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.security.token.JwtAuthenticationToken;
import kim.birthday.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

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

    public UserIntegrationTest() {
        SignupRequest request = new SignupRequest();
        request.setEmail("spring@spring.com");
        request.setPassword("spring12!");
        this.signupRequest = request;
    }

    @Tag("without-signup")
    @Test
    void 회원가입_성공() throws Exception {
        mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                .andDo(document("signup/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 이메일_중복으로_회원가입_실패() throws Exception {
        mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupRequest))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value(AccountErrorCode.EMAIL_IS_EXITS.getMessage()))
                .andDo(document("signup/email-is-exists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Tag("without-signup")
    @Test
    void 유효성_검사_옳지_않은_형식으로_인해_회원가입_실패() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("123");
        request.setPassword("123");

        mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].field", hasItem("email")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("password")))
                .andDo(document("signup/validation-pattern-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Tag("without-signup")
    @Test
    void 유효성_검사_빈값으로_인해_회원가입_실패() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].field", hasItem("email")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("password")))
                .andDo(document("signup/validation-blank",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }
    
    @Test
    void 로그인_성공() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                signupRequest.getEmail(),
                signupRequest.getPassword()
        );

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.expiresAt").exists())
                .andExpect(cookie().exists("refresh_token"))
                .andDo(document("login/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }
    
    @Test
    void 사용자_인증_실패_시_로그인_실패() throws Exception {
        LoginRequest loginRequest = new LoginRequest("123", "123");

        AuthErrorCode errorCode = AuthErrorCode.INVALID_CREDENTIALS;
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.internalCode").value(errorCode.getInternalCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andDo(document("login/invalid-credentials",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 비밀번호_변경_성공_시_200_반환() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "spring123!!",
                "spring123!!"
        );

        mockMvc.perform(post("/password/change")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("change-password/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 비밀번호_재확인_실패_시_400_반환() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest(
                "spring123!!",
                "spring123!"
        );

        mockMvc.perform(post("/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.internalCode").value(AccountErrorCode.PASSWORD_MISMATCH.getInternalCode()))
                .andExpect(jsonPath("$.message").value(AccountErrorCode.PASSWORD_MISMATCH.getMessage()))
                .andDo(document("change-password/mismatch",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 비밀번호_유효성_검사_실패_시_400_반환() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("1!!", "1!!");

        mockMvc.perform(post("/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[*].field", hasItem("newPassword")))
                .andDo(document("change-password/validation-pattern-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }
}
