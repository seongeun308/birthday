package kim.birthday.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import kim.birthday.common.error.TokenErrorCode;
import kim.birthday.common.exception.TokenException;
import kim.birthday.security.token.JwtAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverter implements AuthenticationConverter {

    private static final String HEADER_STRING = "Authorization";
    private static final String PREFIX = "Bearer ";

    @Override
    public Authentication convert(HttpServletRequest request){
        String header = request.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(PREFIX))
            throw new TokenException(TokenErrorCode.PARSE_ERROR);

        String token = header.substring(PREFIX.length());
        return new JwtAuthenticationToken(token);
    }
}
