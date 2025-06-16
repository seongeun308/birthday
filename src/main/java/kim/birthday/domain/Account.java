package kim.birthday.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table
@EntityListeners(AuditingEntityListener.class)
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
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastActiveAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime deleteRequestedAt = null;
    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    public void changePassword(String password) {
        this.password = password;
    }
}