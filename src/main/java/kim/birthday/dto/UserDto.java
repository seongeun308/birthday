package kim.birthday.dto;

import kim.birthday.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserDto {
    private long userId;
    private String publicId;
    private Role role;
    private String email;
    private String password;
}
