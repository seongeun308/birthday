package kim.birthday.service;

import kim.birthday.common.converter.BirthdayConverter;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.domain.Account;
import kim.birthday.domain.Birthday;
import kim.birthday.dto.request.BirthdayAddRequest;
import kim.birthday.repository.AccountRepository;
import kim.birthday.repository.BirthdayRepository;
import kim.birthday.util.PublicIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BirthdayServiceImpl implements BirthdayService {

    private final BirthdayRepository birthdayRepository;
    private final AccountRepository accountRepository;

    @Override
    public String add(BirthdayAddRequest request, Long userId) {
        Birthday birthday = BirthdayConverter.toEntity(request);

        // TODO : refactoring
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AccountException(AccountErrorCode.ACCOUNT_NOT_FOUND));
        birthday.assignAccount(account);

        Birthday saved = birthdayRepository.save(birthday);
        return saved.getPublicId();
    }
}
