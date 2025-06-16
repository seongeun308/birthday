package kim.birthday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ChangePasswordRequest {
    private String newPassword;
    private String confirmPassword;

    public boolean isPasswordConfirmed() {
        return newPassword.equals(confirmPassword);
    }
}