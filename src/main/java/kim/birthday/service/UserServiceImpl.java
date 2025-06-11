package kim.birthday.service;

import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.exception.AccountException;
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

        return new UserDto(savedAccount.getPublicId(), savedAccount.getEmail(), savedAccount.getPassword());
    }

    @Override
    public void checkIfEmailExists(String email) {
        if (accountRepository.existsByEmail(email))
            throw new AccountException(AccountErrorCode.EMAIL_IS_EXITS);
    }
}
