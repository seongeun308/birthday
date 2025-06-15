package kim.birthday.common.exception;

import kim.birthday.common.api.Api;
import kim.birthday.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Api<Void>> handle(AuthException e) {
        ErrorCode errorCode = e.getErrorCode();
        Api<Void> api = Api.error(errorCode);

        log.error("{}", api);

        return ResponseEntity.status(errorCode.getStatus())
                .body(api);
    }
}
