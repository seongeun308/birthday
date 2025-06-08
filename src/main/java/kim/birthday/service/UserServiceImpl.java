package kim.birthday.service;

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
                .role(Role.USER)
                .createdAt(now)
                .deleteRequestedAt(null)
                .isActive(true)
                .lastActiveAt(now)
                .updatedAt(now)
                .build();

        Account savedAccount = accountRepository.save(account);

        return new UserDto(savedAccount.getPublicId(), savedAccount.getEmail(), savedAccount.getPassword());
    }
}
