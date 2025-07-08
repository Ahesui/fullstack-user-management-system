package com.example.gateway_service.utils; // 假设在util包

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return Jwts.builder()
                .subject(username)
                .claim("roles", authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }


    /**
     * 从JWT中解析出用户名
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * 从JWT中解析出角色信息
     */
    @SuppressWarnings("unchecked")
    public List<GrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();
        
        List<Map<String, String>> rolesMap = claims.get("roles", List.class);
        if (rolesMap == null) {
            return Collections.emptyList();
        }
        
        return rolesMap.stream()
                .map(roleMap -> new SimpleGrantedAuthority(roleMap.get("authority")))
                .collect(Collectors.toList());
    }

    /**
     * 验证JWT是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 可以根据不同异常类型进行更细致的日志记录
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }
}