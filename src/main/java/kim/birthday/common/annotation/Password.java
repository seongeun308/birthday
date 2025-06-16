package kim.birthday.common.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kim.birthday.common.validator.PasswordValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "비밀번호는 8~16자로 영문자, 숫자, 특수문자를 포함해야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}