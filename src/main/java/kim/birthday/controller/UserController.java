package kim.birthday.controller;

import jakarta.validation.Valid;
import kim.birthday.common.api.Api;
import kim.birthday.dto.request.SignupRequest;
import kim.birthday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Api<Void>> signup(@Valid @RequestBody SignupRequest request) {
        userService.checkIfEmailExists(request.getEmail());
        userService.signup(request);

        HttpStatus status = HttpStatus.CREATED;
        Api<Void> api = Api.success(status, null);

        return ResponseEntity.status(status)
                .body(api);
    }
}
