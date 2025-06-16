package kim.birthday.dto.request;

import kim.birthday.common.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ChangePasswordRequest {
    @Password
    private String newPassword;
    private String confirmPassword;

    public boolean isPasswordMatch() {
        return newPassword.equals(confirmPassword);
    }
}