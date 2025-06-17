package kim.birthday.common.exception.handler;

import kim.birthday.common.api.Api;
import kim.birthday.common.exception.ExceptionResponseBuilder;
import kim.birthday.common.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TokenExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Api<Void>> handle(TokenException e) {
        return ExceptionResponseBuilder.build(e);
    }
}
