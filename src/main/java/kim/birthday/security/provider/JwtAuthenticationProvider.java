package kim.birthday.security.provider;

import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.LoginSession;
import kim.birthday.security.token.JwtAuthenticationToken;
import kim.birthday.service.TokenValidationService;
import kim.birthday.util.AuthenticationUserUtils;
import kim.birthday.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;
    private final TokenValidationService tokenValidationService;
    private final AuthenticationUserUtils authenticationUserUtils;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = authentication.getCredentials().toString();
        String publicId = jwtProvider.getPayload(accessToken).getSubject();

        tokenValidationService.validateAccessToken(accessToken);

        AuthenticatedUser user = authenticationUserUtils.getAuthenticatedUserByPublicId(publicId);
        LoginSession loginSession = new LoginSession(user, accessToken);
        return new JwtAuthenticationToken(loginSession, List.of(user.getRole().toAuthority()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
