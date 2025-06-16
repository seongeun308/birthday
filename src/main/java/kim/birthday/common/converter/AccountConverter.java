package kim.birthday.common.converter;

import kim.birthday.domain.Account;
import kim.birthday.dto.UserDto;

public class AccountConverter {
    public static UserDto toUserDto(Account account) {
        return new UserDto(
                account.getId(),
                account.getPublicId(),
                account.getRole(),
                account.getEmail(),
                account.getPassword()
        );
    }
}
