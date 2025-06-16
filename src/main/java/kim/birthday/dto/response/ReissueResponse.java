package kim.birthday.dto.response;

import kim.birthday.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ReissueResponse {
    private String accessToken;
    private String expiresAt;

    public ReissueResponse(TokenDto accessTokenDto) {
        this.accessToken = accessTokenDto.getToken();
        this.expiresAt = accessTokenDto.getExpiresAt();
    }
}