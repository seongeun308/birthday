package kim.birthday.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.LoginSession;
import kim.birthday.dto.TokenPair;
import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.BirthdayAddRequest;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.security.token.JwtAuthenticationToken;
import kim.birthday.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class BirthdayIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    private static final String PATH = "/birthday";

    @BeforeEach
    void setUp() {
        // TODO : 리팩토링
        // 회원가입
        SignupRequest request = new SignupRequest();
        request.setEmail("spring@spring.com");
        request.setPassword("spring12!");
        UserDto userDto = userService.signup(request);
        AuthenticatedUser user = new AuthenticatedUser(userDto.getUserId(), userDto.getPublicId(), userDto.getRole());

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(user, List.of(user.getRole().toAuthority()));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 로그인
        TokenPair tokenPair = userService.login(user);
        LoginSession loginSession = new LoginSession(user, tokenPair.getAccessToken().getToken());
        JwtAuthenticationToken loginSessionToken = new JwtAuthenticationToken(loginSession, List.of(user.getRole().toAuthority()));
        SecurityContextHolder.getContext().setAuthentication(loginSessionToken);
    }

    @Test
    void 생일_등록_성공하면_201_반환() throws Exception {
        BirthdayAddRequest request = new BirthdayAddRequest("홍길동", "2025-07-25");

        mockMvc.perform(post(PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("birthday/add/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void 생일_등록_요청_유효성_검사_실패_시_400_반환() throws Exception {
        BirthdayAddRequest request = new BirthdayAddRequest(null, "2025-07");

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andDo(document("birthday/add/validation-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }
}
