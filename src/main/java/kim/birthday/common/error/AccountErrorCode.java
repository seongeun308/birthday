package kim.birthday.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    EMAIL_IS_EXITS(HttpStatus.CONFLICT, "U001", "이미 존재하는 이메일입니다.")
    ;

    private final HttpStatus status;
    private final String internalCode;
    private final String message;
}
