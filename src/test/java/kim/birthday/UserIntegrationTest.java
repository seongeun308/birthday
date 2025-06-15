package kim.birthday;

import com.fasterxml.jackson.databind.ObjectMapper;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.dto.request.LoginRequest;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.dto.response.LoginResponse;
import kim.birthday.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import static org.hamcrest.Matchers.*;
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
    private final static String VALID_EMAIL = "spring@email.com";
    private final static String VALID_PASSWORD = "spring123!";

    @Test
    void 회원가입_성공() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail(VALID_EMAIL);
        request.setPassword(VALID_PASSWORD);

        mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                .andDo(document("signup/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 이메일_중복으로_회원가입_실패() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail(VALID_EMAIL);
        request.setPassword(VALID_PASSWORD);
        userService.signup(request);

        mockMvc.perform(post("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value(AccountErrorCode.EMAIL_IS_EXITS.getMessage()))
                .andDo(document("signup/email-is-exists",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 유효성_검사_옳지_않은_형식으로_인해_회원가입_실패() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("123");
        request.setPassword("123");

        userService.signup(request);

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

    @Test
    void 유효성_검사_빈값으로_인해_회원가입_실패() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("");
        request.setPassword("");

        userService.signup(request);

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
        회원가입_성공();
        LoginRequest loginRequest = new LoginRequest(VALID_EMAIL, VALID_PASSWORD);

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
        회원가입_성공();
        LoginRequest loginRequest = new LoginRequest("123", VALID_PASSWORD);


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
}
