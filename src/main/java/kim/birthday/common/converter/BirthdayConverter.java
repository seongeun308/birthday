package kim.birthday.common.converter;

import kim.birthday.domain.Birthday;
import kim.birthday.dto.request.BirthdayAddRequest;

public class BirthdayConverter {
    public static Birthday toEntity(BirthdayAddRequest request) {
        return Birthday.builder()
                .name(request.getName())
                .birth(request.getBirth())
                .build();
    }
}
