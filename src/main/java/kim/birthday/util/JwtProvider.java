package kim.birthday.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kim.birthday.common.error.TokenErrorCode;
import kim.birthday.common.exception.TokenException;
import kim.birthday.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final int accessExpiresIn;
    private final int refreshExpiresIn;
    private final SecretKey secretKey;

    public JwtProvider(
            @Value("${jwt.secret_key}") String secretKey,
            @Value("${jwt.expiration-minutes}") int minutes,
            @Value("${jwt.expiration-days}") int days
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessExpiresIn = minutes * 60;
        this.refreshExpiresIn = days * 24 * 60 * 60;
    }

    public TokenDto generateAccessToken(final String publicId, final Map<String, Object> claims) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusSeconds = now.plusSeconds(accessExpiresIn);
        Date expiration = DateConverter.toDate(plusSeconds);

        String token = Jwts.builder()
                .signWith(secretKey)
                .issuedAt(DateConverter.toDate(now))
                .expiration(expiration)
                .subject(publicId)
                .claims(claims)
                .compact();

        return new TokenDto(token, plusSeconds.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public TokenDto generateRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusSeconds = now.plusSeconds(refreshExpiresIn);
        Date expiration = DateConverter.toDate(plusSeconds);

        String token = Jwts.builder()
                .signWith(secretKey)
                .issuedAt(DateConverter.toDate(now))
                .expiration(expiration)
                .compact();

        return new TokenDto(token, plusSeconds.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public void validateToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException ignored) {
            throw new TokenException(TokenErrorCode.EXPIRED);
        } catch (SecurityException ignored) {
            throw new TokenException(TokenErrorCode.INVALID_SIGNATURE);
        } catch (MalformedJwtException ignored) {
            throw new TokenException(TokenErrorCode.MALFORMED);
        } catch (JwtException ignored) {
            throw new TokenException(TokenErrorCode.PARSE_ERROR);
        }
    }
}
