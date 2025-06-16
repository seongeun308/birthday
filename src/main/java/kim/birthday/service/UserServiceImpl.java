package kim.birthday.service;

import kim.birthday.common.converter.AccountConverter;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.error.AuthErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.common.exception.AuthException;
import kim.birthday.domain.Account;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.ChangePasswordRequest;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.repository.AccountRepository;
import kim.birthday.store.PasswordVerificationStore;
import kim.birthday.util.PublicIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordVerificationStore passwordVerificationStore;

    @Override
    public UserDto signup(SignupRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Account account = Account.builder()
                .publicId(PublicIdGenerator.generatePublicId())
                .email(request.getEmail())
                .password(encodedPassword)
                .build();

        Account savedAccount = accountRepository.save(account);

        log.info("[회원가입] 사용자 [{}] 성공", savedAccount.getPublicId());

        return AccountConverter.toUserDto(savedAccount);
    }

    @Override
    public void checkIfEmailExists(String email) {
        if (accountRepository.existsByEmail(email))
            throw new AccountException(AccountErrorCode.EMAIL_IS_EXITS);
    }

    @Override
    public void verifyPassword(Long userId, String rowPassword) {
        Account account = getAccountById(userId);

        if (!passwordEncoder.matches(rowPassword, account.getPassword()))
            throw new AuthException(AuthErrorCode.MISMATCH_PASSWORD);

        passwordVerificationStore.markVerified(userId);
    }

    private Account getAccountById(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Override
    public void changePassword(AuthenticatedUser user, ChangePasswordRequest request) {
        log.info("[비밀번호변경] 사용자 [{}] 시도", user.getPublicId());

        passwordVerificationStore.verifyOrThrow(user.getUserId());

        Account account = getAccountById(user.getUserId());
        account.changePassword(passwordEncoder.encode(request.getNewPassword()));

        passwordVerificationStore.clear(user.getUserId());

        log.info("[비밀번호변경] 사용자 [{}] 성공", user.getPublicId());
    }


}