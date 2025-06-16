package kim.birthday.util;

import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AuthException;
import kim.birthday.domain.Account;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUserUtils {

    private final AccountRepository accountRepository;

    public AuthenticatedUser getAuthenticatedUserByPublicId(String publicId) {
        Account account = accountRepository.findByPublicId(publicId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.ACCOUNT_NOT_FOUND));

        return new AuthenticatedUser(account.getId(), account.getPublicId(), account.getRole());
    }
}
