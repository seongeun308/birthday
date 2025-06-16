package kim.birthday.dto;

import kim.birthday.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {
    private final long userId;
    private final String publicId;
    private final Role role;
}
