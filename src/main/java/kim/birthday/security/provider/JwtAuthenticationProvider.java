package kim.birthday.security.provider;

import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.security.token.JwtAuthenticationToken;
import kim.birthday.service.TokenBlacklistService;
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
    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationUserUtils authenticationUserUtils;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = authentication.getCredentials().toString();

        jwtProvider.validateToken(accessToken);
        tokenBlacklistService.throwIfBlacklisted(accessToken);

        String publicId = jwtProvider.getPayload(accessToken).getSubject();
        AuthenticatedUser user = authenticationUserUtils.getAuthenticatedUserByPublicId(publicId);
        return new JwtAuthenticationToken(user, List.of(user.getRole().toAuthority()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
