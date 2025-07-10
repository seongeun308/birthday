package kim.birthday.service;

import kim.birthday.domain.Account;
import kim.birthday.dto.request.BirthdayAddRequest;

public interface BirthdayService {
    String add(BirthdayAddRequest request, Account account);
}
