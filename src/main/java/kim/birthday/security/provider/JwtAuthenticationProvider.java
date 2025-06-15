package kim.birthday.security.provider;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AuthException;
import kim.birthday.domain.Account;
import kim.birthday.repository.AccountRepository;
import kim.birthday.security.token.JwtAuthenticationToken;
import kim.birthday.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;
    private final AccountRepository accountRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = authentication.getCredentials().toString();

        jwtProvider.validateToken(accessToken);

        String publicId = jwtProvider.getPayload(accessToken).getSubject();
        Account account = accountRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        return new JwtAuthenticationToken(account.getId(), List.of(account.getRole().toAuthority()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
