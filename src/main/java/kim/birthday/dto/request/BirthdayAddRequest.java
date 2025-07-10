package kim.birthday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BirthdayAddRequest {
    private String name;
    private LocalDate birth;
}
