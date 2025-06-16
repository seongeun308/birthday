package kim.birthday.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ChangePasswordRequest {
    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 8~16자로 영문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String newPassword;
    private String confirmPassword;

    public boolean isPasswordConfirmed() {
        return newPassword.equals(confirmPassword);
    }
}