package kim.birthday.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kim.birthday.common.api.Api;
import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class EmailPasswordAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        ErrorCode errorCode = mapExceptionToErrorCode(exception);

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");

        Api<Void> api = Api.error(errorCode);

        log.error("{}", api);

        new ObjectMapper().writeValue(response.getWriter(), api);
    }

    private ErrorCode mapExceptionToErrorCode(AuthenticationException e) {
        if (e instanceof BadCredentialsException) return AuthErrorCode.INVALID_CREDENTIALS;
        if (e instanceof AuthenticationServiceException) return AuthErrorCode.INVALID_REQUEST;
        return AuthErrorCode.UNKNOWN_ERROR;
    }
}