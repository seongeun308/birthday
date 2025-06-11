package kim.birthday.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();

    String getInternalCode();

    String getMessage();
}
