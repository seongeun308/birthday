package kim.birthday.common.exception.handler;

import kim.birthday.common.api.Api;
import kim.birthday.common.exception.AccountException;
import kim.birthday.common.exception.ExceptionResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AccountExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Api<Void>> handle(AccountException e) {
        return ExceptionResponseBuilder.build(e);
    }
}
