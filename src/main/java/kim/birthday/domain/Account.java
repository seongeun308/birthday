package kim.birthday.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Table(indexes = {
        @Index(name = "idx_account_email", columnList = "email", unique = true)
})
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String publicId;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;
    @Column(nullable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastActiveAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime deleteRequestedAt = null;
    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;
}