package kim.birthday.common.converter;

import kim.birthday.domain.Birthday;
import kim.birthday.dto.request.BirthdayAddRequest;
import kim.birthday.util.PublicIdGenerator;

import java.time.LocalDate;

public class BirthdayConverter {
    public static Birthday toEntity(BirthdayAddRequest request) {
        return Birthday.builder()
                .name(request.getName())
                .birth(LocalDate.parse(request.getBirth()))
                .publicId(PublicIdGenerator.generatePublicId())
                .build();
    }
}
