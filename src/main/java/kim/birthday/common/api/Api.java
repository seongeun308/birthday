package kim.birthday.common.api;

import kim.birthday.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Api<T> {
    private final int statusCode;
    private final String internalCode;
    private final String message;
    private final List<FieldErrorDetail> errors;
    private final T data;

    public static <T> Api<T> success(HttpStatus httpStatus, T data) {
        return new Api<>(
                httpStatus.value(),
                null,
                "성공",
                null,
                data
        );
    }

    public static <T> Api<T> success(T data) {
        return new Api<>(
                HttpStatus.OK.value(),
                null,
                "성공",
                null,
                data
        );
    }

    public static <T> Api<T> error(ErrorCode errorCode) {
        return new Api<>(
                errorCode.getStatus().value(),
                errorCode.getInternalCode(),
                errorCode.getMessage(),
                null,
                null
        );
    }

    public static <T> Api<T> error(HttpStatus httpStatus, List<FieldErrorDetail> errors) {
        return new Api<>(
                httpStatus.value(),
                String.valueOf(httpStatus.value()),
                "유효성 검사 실패",
                errors,
                null
        );
    }
}
