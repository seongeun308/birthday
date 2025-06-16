package kim.birthday.common.exception;

import kim.birthday.common.error.AccountErrorCode;

public class AccountException extends BaseException {
    public AccountException(AccountErrorCode errorCode) {
        super(errorCode);
    }
}
