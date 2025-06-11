package kim.birthday.common.api;

import kim.birthday.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Api<T> {
    private final HttpStatus status;
    private final int code;
    private final String message;
    private final List<FieldErrorDetail> errors;
    private final T data;

    public static <T> Api<T> success(HttpStatus httpStatus, T data) {
        return new Api<>(
                httpStatus,
                httpStatus.value(),
                "성공",
                null,
                data
        );
    }

    public static <T> Api<T> fail(ErrorCode errorCode) {
        return new Api<>(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                null
        );
    }

    public static <T> Api<T> fail(HttpStatus httpStatus, List<FieldErrorDetail> errors) {
        return new Api<>(
                httpStatus,
                httpStatus.value(),
                "유효성 검사 실패",
                errors,
                null
        );
    }

}
