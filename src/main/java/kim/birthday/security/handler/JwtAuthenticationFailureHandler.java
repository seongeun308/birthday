package kim.birthday.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kim.birthday.common.api.Api;
import kim.birthday.common.error.ErrorCode;
import kim.birthday.common.exception.ErrorCoded;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        ErrorCoded authException = (ErrorCoded) exception;
        ErrorCode errorCode = authException.getErrorCode();

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Api<Void> api = Api.error(errorCode);

        log.error("{}", api);

        new ObjectMapper().writeValue(response.getWriter(), api);
    }

}