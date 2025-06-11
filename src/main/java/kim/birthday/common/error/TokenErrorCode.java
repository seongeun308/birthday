package kim.birthday.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenErrorCode {
    EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH003", "만료된 토큰입니다."),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "AUTH001", "토큰 서명이 유효하지 않습니다."),
    MALFORMED(HttpStatus.BAD_REQUEST, "AUTH002", "잘못된 토큰 형식입니다."),
    PARSE_ERROR(HttpStatus.UNAUTHORIZED, "AUTH999", "토큰 파싱 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String internalCode;
    private final String message;
}
