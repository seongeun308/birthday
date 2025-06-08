package kim.birthday.common.exception;

import kim.birthday.common.error.AccountErrorCode;

public class AccountException extends RuntimeException {
  public AccountException(AccountErrorCode errorCode) {
    super(errorCode.getMessage());
  }
}
