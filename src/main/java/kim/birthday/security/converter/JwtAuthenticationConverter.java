package kim.birthday.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import kim.birthday.security.token.JwtAuthenticationToken;
import kim.birthday.util.TokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String accessToken = TokenUtils.extractAccessToken(request);
        return new JwtAuthenticationToken(accessToken);
    }
}
