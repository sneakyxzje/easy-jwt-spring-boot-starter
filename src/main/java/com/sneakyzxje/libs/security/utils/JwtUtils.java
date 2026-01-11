package com.sneakyzxje.libs.security.utils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sneakyzxje.libs.security.dto.RefreshTokenDTO;
import com.sneakyzxje.libs.security.properties.SecurityProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtils {
    private final SecurityProperties securityProperties;
    
    public JwtUtils(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(securityProperties.getAuthentication().getJwtSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims extractAllClaim(String token) {
        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    public List<String> extractRoles(String token) {
        return extractAllClaim(token).get("roles", List.class);
    }

    public String extractUsername(String token) {
        return extractAllClaim(token).getSubject();
    }


    public boolean validateToken(String token) {
        try {
        Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        
        return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired! " + e.getMessage());
            return false; 
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public String createAccessToken(String subject, List<String> roles) {
        long now = System.currentTimeMillis();
        long exp = now + (securityProperties.getAuthentication().getJwtExpiration() * 1000);
        Date issuedAt = new Date(now);
        Date expiredAt = new Date(exp);
        return Jwts.builder()
        .setSubject(subject)
        .claim("roles", roles)
        .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
        .setIssuedAt(issuedAt)
        .setExpiration(expiredAt)
        .compact();
    }

    public RefreshTokenDTO createRefreshToken() {
        String refreshToken = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        long exp = now + (securityProperties.getAuthentication().getRefreshTokenExpiration() * 1000);
        Date expiryDate = new Date(exp);

        return new RefreshTokenDTO(refreshToken, expiryDate);
    }

}
