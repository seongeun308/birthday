package kim.birthday.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Birthday {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long birthdayId;
    private String publicId;
    private Long userId;
    private String name;
    private LocalDate birthday;
}
