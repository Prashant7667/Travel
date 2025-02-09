package prashant.example.rideSharing.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // -------------------------------------------------------------
    // In production, store this key securely (e.g., environment var)
    // -------------------------------------------------------------
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Example: 1 day expiration in milliseconds
    private final long jwtExpirationMs = 86400000;

    /**
     * Generate a JWT token with the user's email as the subject.
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    /**
     * Validate the token's signature and expiration.
     * Return the email (subject) if valid, or throw an exception if invalid.
     */
    public String validateTokenAndGetEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // The email we stored in .setSubject()
        } catch (JwtException ex) {
            // Could be ExpiredJwtException, MalformedJwtException, etc.
            throw new RuntimeException("Invalid or expired JWT token");
        }
    }
}
