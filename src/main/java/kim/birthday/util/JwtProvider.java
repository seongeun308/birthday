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
        this.accessExpiresIn = minutes * 60 * 1000;
        this.refreshExpiresIn = days * 24 * 60 * 60 * 1000;
    }

    public TokenDto generateAccessToken(final String publicId, final Map<String, Object> claims) {
        return generateToken(publicId, claims, TokenType.ACCESS);
    }

    public TokenDto generateRefreshToken(final String publicId, final Map<String, Object> claims) {
        return generateToken(publicId, claims, TokenType.REFRESH);
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

    private TokenDto generateToken(final String publicId, final Map<String, Object> claims, final TokenType type) {
        LocalDateTime now = LocalDateTime.now();
        int expiresIn = (type == TokenType.ACCESS) ? accessExpiresIn : refreshExpiresIn;
        Date expiration = DateConverter.toDate(now.plusSeconds(expiresIn));

        String token = Jwts.builder()
                .signWith(secretKey)
                .issuedAt(DateConverter.toDate(now))
                .expiration(expiration)
                .subject(publicId)
                .claims(claims)
                .compact();

        return new TokenDto(token, expiration.toString());
    }

    private enum TokenType {
        ACCESS, REFRESH
    }
}
