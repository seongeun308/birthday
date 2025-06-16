package kim.birthday.common.exception.handler;

import kim.birthday.common.api.Api;
import kim.birthday.common.api.FieldErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Api<Void>> handleUserException(MethodArgumentNotValidException e) {
        List<FieldErrorDetail> errorDetails = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldErrorDetail::from)
                .toList();

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Api<Void> api = Api.error(status, errorDetails);

        log.error("{}", api);

        return ResponseEntity.status(status)
                .body(api);
    }
}