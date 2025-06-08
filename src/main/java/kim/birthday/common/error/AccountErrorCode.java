package kim.birthday.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountErrorCode {
    EMAIL_IS_EXITS(HttpStatus.CONFLICT, HttpStatus.CONFLICT.value(), "이미 존재하는 이메일입니다.")
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;
}
