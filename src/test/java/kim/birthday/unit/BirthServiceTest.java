package kim.birthday.unit;

import kim.birthday.domain.Account;
import kim.birthday.domain.Birthday;
import kim.birthday.dto.request.BirthdayAddRequest;
import kim.birthday.repository.AccountRepository;
import kim.birthday.repository.BirthdayRepository;
import kim.birthday.service.BirthdayServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BirthServiceTest {

    @InjectMocks
    private BirthdayServiceImpl birthdayService;
    @Mock
    private BirthdayRepository birthdayRepository;
    @Mock
    private AccountRepository accountRepository;


    @Test
    void 생일_등록_성공() {
        BirthdayAddRequest request = new BirthdayAddRequest("홍길동", LocalDate.of(2025, 7, 10));
        Account account = Account.builder().build();
        Birthday birthday = Birthday.builder().publicId("testPublicId").build();
        given(birthdayRepository.save(any(Birthday.class))).willReturn(birthday);
        given(accountRepository.findById(any())).willReturn(Optional.of(account));

        String publicId = birthdayService.add(request, 1L);

        assertFalse(publicId.isEmpty());
    }
}
