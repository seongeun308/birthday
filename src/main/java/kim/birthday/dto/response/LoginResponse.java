package kim.birthday.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String expiresAt;
}
