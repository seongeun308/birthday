package kim.birthday.service;

import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.UserDto;
import kim.birthday.dto.request.ChangePasswordRequest;
import kim.birthday.dto.request.SignupRequest;

public interface UserService {
    UserDto signup(SignupRequest request);

    void checkIfEmailExists(String email);

    void verifyPassword(Long userId, String rowPassword);

    void changePassword(AuthenticatedUser user, ChangePasswordRequest request);
}
