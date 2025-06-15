package kim.birthday.service;

import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.SignupRequest;

public interface UserService {
    UserDto signup(SignupRequest request);

    void checkIfEmailExists(String email);

    void isMatchPassword(long userId, String rowPassword);
}
