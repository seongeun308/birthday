package kim.birthday.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN003", "만료된 토큰입니다."),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "TOKEN001", "토큰 서명이 유효하지 않습니다."),
    MALFORMED(HttpStatus.BAD_REQUEST, "TOKEN002", "잘못된 토큰 형식입니다."),
    MISSING_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN003", "토큰이 누락되었습니다."),
    PARSE_ERROR(HttpStatus.UNAUTHORIZED, "TOKEN999", "토큰 파싱 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String internalCode;
    private final String message;
}
