package kim.birthday.common.exception;

import kim.birthday.common.error.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException implements ErrorCoded {

    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}