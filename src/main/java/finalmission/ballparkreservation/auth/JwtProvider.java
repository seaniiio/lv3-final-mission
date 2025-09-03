package finalmission.ballparkreservation.auth;

import finalmission.ballparkreservation.member.Member;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String SECRET_KEY = "foiehfiowhefoihadskfjnsdkjnkjdcbHpqowediojLIehfdxofbkvjuhqpowiehlkcnxvl";

    public String createToken(final Member member) {
        return Jwts.builder()
                .setSubject(member.getId().toString())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    public boolean isValidToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build().parse(token);
            return true;
        } catch (final JwtException e) {
            return false;
        }
    }

    public String getSubjectByToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException("토큰 정보가 올바르지 않습니다.");
        }
    }
}
