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

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("canRead", user.getCanRead() != null ? user.getCanRead() : false)
                .claim("canEdit", user.getCanEdit() != null ? user.getCanEdit() : false)
                .setIssuedAt(now)
                .setExpiration(expiryDate);

        if (user.getCompany() != null) {
            jwtBuilder.claim("companyId", user.getCompany().getId());
        }

        return jwtBuilder
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

    // Method to get company ID from JWT
    public Long getCompanyIdFromJWT(String token) {
        Claims claims = parseToken(token);
        Object companyId = claims.get("companyId");
        if (companyId != null) {
            if (companyId instanceof Number) {
                return ((Number) companyId).longValue();
            } else if (companyId instanceof String) {
                return Long.parseLong((String) companyId);
            }
        }
        return null;
    }

    // New method to get canRead permission from JWT
    public Boolean getCanReadFromJWT(String token) {
        Claims claims = parseToken(token);
        return claims.get("canRead", Boolean.class);
    }

    // New method to get canEdit permission from JWT
    public Boolean getCanEditFromJWT(String token) {
        Claims claims = parseToken(token);
        return claims.get("canEdit", Boolean.class);
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

    public boolean isTokenExpired(String token) {
        try {
            parseToken(token);
            return false;
        } catch (ExpiredJwtException ex) {
            return true;
        } catch (Exception ex) {
            return false; // If other exceptions, not expired
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}