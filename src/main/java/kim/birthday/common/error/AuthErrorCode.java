package kim.birthday.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "AUTH002", "잘못된 로그인 요청입니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.FORBIDDEN, "AUTH003", "토큰에 지정된 사용자가 존재하지 않습니다."),
    MISMATCH_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH004", "비밀번호가 일치하지 않습니다."),
    PASSWORD_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "AUTH005", "비밀번호 인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH006", "유효하지 않은 토큰입니다"),
    BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH007", "유효하지 않은 토큰입니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH999", "알 수 없는 인증 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String internalCode;
    private final String message;
}
