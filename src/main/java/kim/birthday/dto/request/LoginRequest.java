package kim.birthday.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequest {
    private final String email;
    private final String password;

    @JsonCreator
    public LoginRequest(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password
    ) {
        this.email = email;
        this.password = password;
    }
}