package kim.birthday.common.exception;

import kim.birthday.common.error.TokenErrorCode;
import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public TokenException(TokenErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
