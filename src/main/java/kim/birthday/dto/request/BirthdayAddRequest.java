package kim.birthday.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BirthdayAddRequest {
    @NotBlank(message = "생일자 이름은 필수값 입니다.")
    private String name;
    @NotNull(message = "생일은 필수값입니다.")
    @Pattern(
            regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "날짜 형식이 유효하지 않습니다."
    )
    private String birth;
}
