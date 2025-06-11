package kim.birthday.common.exception;

import kim.birthday.common.error.AccountErrorCode;
import lombok.Getter;

@Getter
public class AccountException extends RuntimeException {

  private AccountErrorCode errorCode;

  public AccountException(AccountErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
