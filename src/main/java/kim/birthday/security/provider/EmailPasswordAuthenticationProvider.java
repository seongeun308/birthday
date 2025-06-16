package kim.birthday.security.provider;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.domain.Account;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@RequiredArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();

        // 사용자 인증 로직
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException(""));

        // 인증 실패
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadCredentialsException(AuthErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        AuthenticatedUser user = new AuthenticatedUser(account.getId(), account.getPublicId(), account.getRole());

        // 인증 성공 → 인증된 Authentication 반환
        return new UsernamePasswordAuthenticationToken(
                user, null, Collections.singleton(account.getRole().toAuthority())
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}