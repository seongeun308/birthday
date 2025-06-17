package kim.birthday.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "RT", timeToLive = 7 * 24 * 60 * 60 + 300) // 7일 + 5분
@ToString
public class RefreshToken {
    @Id
    private Long userId;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
