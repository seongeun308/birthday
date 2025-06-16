package kim.birthday.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountErrorCode implements ErrorCode {
    EMAIL_IS_EXITS(HttpStatus.CONFLICT, "U001", "이미 존재하는 이메일입니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "해당 사용자 계정이 존재하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U003", "입력한 비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String internalCode;
    private final String message;
}
