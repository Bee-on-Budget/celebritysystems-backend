package com.celebritysystems.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.celebritysystems.entity.User;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TokenProvider {

    private final SecretKey jwtSecretKey;
    private final long jwtExpirationMs;

    public TokenProvider(@Value("${app.jwt.expiration-hours:24}") long jwtExpirationHours) {
        this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtExpirationMs = TimeUnit.HOURS.toMillis(jwtExpirationHours);
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
//                .claim("roles", new ArrayList<>(user.getRoles())) // Convert Set to List
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public String getUserEmailFromJWT(String token) {
        return parseToken(token).get("email", String.class);
    }

    public String getUsernameFromJWT(String token) {
        return parseToken(token).get("username", String.class);
    }

    public Set<String> getRolesFromJWT(String token) {
        Claims claims = parseToken(token);
        
     
        String role = claims.get("role", String.class);
        if (role != null) {
            role = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            return Collections.singleton(role);
        }
        
        return Collections.emptySet();
    }
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SignatureException ex) {
            // Invalid JWT signature
        } catch (MalformedJwtException ex) {
            // Invalid JWT token
        } catch (ExpiredJwtException ex) {
            // Expired JWT token
        } catch (UnsupportedJwtException ex) {
            // Unsupported JWT token
        } catch (IllegalArgumentException ex) {
            // JWT claims string is empty
        }
        return false;
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}