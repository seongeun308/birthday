package kim.birthday.service;

import kim.birthday.common.converter.BirthdayConverter;
import kim.birthday.domain.Account;
import kim.birthday.domain.Birthday;
import kim.birthday.dto.request.BirthdayAddRequest;
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

    @Override
    public String add(BirthdayAddRequest request, Account account) {
        Birthday birthday = BirthdayConverter.toEntity(request);
        birthday.assignAccount(account);

        Birthday saved = birthdayRepository.save(birthday);
        return saved.getPublicId();
    }
}
