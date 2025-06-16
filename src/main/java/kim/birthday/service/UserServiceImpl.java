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
import kim.birthday.util.PublicIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
    public void isMatchPassword(AuthenticatedUser user, String rowPassword) {
        Account account = accountRepository.findById(user.getUserId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        if (!passwordEncoder.matches(rowPassword, account.getPassword()))
            throw new AuthException(AuthErrorCode.MISMATCH_PASSWORD);
    }

    @Override
    public void changePassword(AuthenticatedUser user, ChangePasswordRequest request) {
        log.info("[비밀번호변경] 사용자 [{}] 시도", user.getPublicId());
        
        // 비밀번호 비교
        if (!request.isPasswordConfirmed()) {
            log.warn("[비밀번호변경] 사용자 [{}] 실패 - 비밀번호 불일치", user.getPublicId());
            throw new AccountException(AccountErrorCode.PASSWORD_MISMATCH);
        }
        // 비밀번호 수정
        Account account = accountRepository.findById(user.getUserId())
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));

        account.changePassword(passwordEncoder.encode(request.getNewPassword()));

        log.info("[비밀번호변경] 사용자 [{}] 성공", user.getPublicId());
    }
}