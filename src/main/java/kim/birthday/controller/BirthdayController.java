package kim.birthday.controller;

import kim.birthday.common.api.Api;
import kim.birthday.common.error.AccountErrorCode;
import kim.birthday.common.exception.AccountException;
import kim.birthday.domain.Account;
import kim.birthday.dto.AuthenticatedUser;
import kim.birthday.dto.LoginSession;
import kim.birthday.dto.request.BirthdayAddRequest;
import kim.birthday.repository.AccountRepository;
import kim.birthday.service.BirthdayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/birthday")
public class BirthdayController {

    private final BirthdayService birthdayService;
    private static final String BASE_LOCATION = "/birthday/{publicId}";

    @PostMapping
    public ResponseEntity<Api<Void>> addBirthday(
            @RequestBody BirthdayAddRequest request,
            @AuthenticationPrincipal LoginSession loginSession) {
        String publicId = birthdayService.add(request, loginSession.userId());

        URI location = UriComponentsBuilder
                .fromPath(BASE_LOCATION)
                .buildAndExpand(publicId)
                .toUri();

        return ResponseEntity.created(location)
                .body(Api.created());
    }

}
