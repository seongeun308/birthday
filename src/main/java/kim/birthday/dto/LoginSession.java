package kim.birthday.dto;

import kim.birthday.domain.Role;
import lombok.Getter;

public record LoginSession(Long userId, String publicId, Role role, String accessToken) {

    public LoginSession(AuthenticatedUser user, String accessToken) {
        this(user.getUserId(), user.getPublicId(), user.getRole(), accessToken);
    }
}
