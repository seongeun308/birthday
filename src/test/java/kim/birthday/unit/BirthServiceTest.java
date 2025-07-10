package kim.birthday.unit;

import kim.birthday.domain.Account;
import kim.birthday.dto.request.BirthdayAddRequest;
import kim.birthday.service.BirthdayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class BirthServiceTest {

    @Autowired
    private BirthdayService birthdayService;

    @Test
    void 생일_등록_성공() {
        BirthdayAddRequest request = new BirthdayAddRequest("홍길동", LocalDate.of(2025, 7, 10));
        Account account = Account.builder()
                .email("testtest")
                .password("testtest")
                .build();

        String publicId = birthdayService.add(request, account);

        assertFalse(publicId.isEmpty());
    }
}
