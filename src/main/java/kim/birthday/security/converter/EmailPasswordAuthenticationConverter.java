package kim.birthday.security.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.dto.request.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class EmailPasswordAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(
                    request.getInputStream(),
                    LoginRequest.class
            );
            return new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AuthenticationServiceException(AuthErrorCode.INVALID_REQUEST.getMessage());
        }
    }
}