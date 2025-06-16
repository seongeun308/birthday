package kim.birthday.common.exception.handler;

import kim.birthday.common.api.Api;
import kim.birthday.common.exception.AuthException;
import kim.birthday.common.exception.ExceptionResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Api<Void>> handle(AuthException e) {
        return ExceptionResponseBuilder.build(e);
    }
}
