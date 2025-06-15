package kim.birthday.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserDto {
    private long userId;
    private String publicId;
    private String email;
    private String password;
}
