package kim.birthday.common.exception;

import kim.birthday.common.api.Api;
import kim.birthday.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ExceptionResponseBuilder {

    public static <T extends ErrorCoded> ResponseEntity<Api<Void>> build(T e) {
        ErrorCode errorCode = e.getErrorCode();
        Api<Void> api = Api.error(errorCode);

        log.error("{}", api);

        return ResponseEntity.status(errorCode.getStatus()).body(api);
    }
}