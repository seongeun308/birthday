package kim.birthday.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TokenPair {
    private final TokenDto accessToken;
    private final TokenDto refreshToken;
}
