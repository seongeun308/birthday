package kim.birthday.service;

import kim.birthday.common.converter.AccountConverter;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.common.exception.AuthException;
import kim.birthday.domain.Account;
import kim.birthday.domain.Role;
import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.repository.AccountRepository;
import kim.birthday.util.PublicIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto signup(SignupRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        LocalDateTime now = LocalDateTime.now();
        Account account = Account.builder()
                .publicId(PublicIdGenerator.generatePublicId())
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        Account savedAccount = accountRepository.save(account);

        // TODO: refactoring #1 account 저장 결과 로그 찍기

        return AccountConverter.toUserDto(savedAccount);
    }

    @Override
    public void checkIfEmailExists(String email) {
        if (accountRepository.existsByEmail(email))
            throw new AccountException(AccountErrorCode.EMAIL_IS_EXITS);
    }

    @Override
    public void isMatchPassword(long userId, String rowPassword) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        if (!passwordEncoder.matches(rowPassword, account.getPassword()))
            throw new AuthException(AuthErrorCode.MISMATCH_PASSWORD);
    }
}
