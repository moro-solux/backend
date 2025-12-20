package com.example.moro.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    // 설정파일(yml)에서 비밀키를 가져오거나 임시 문자열을 사용합니다.
    @Value("${jwt.secret:vmfhaltjskdbstjavyansjungskwodydrnwhskqbzl}")
    private String salt;

    private Key secretKey;

    // 토큰 유효시간: 1시간
    private final long exp = 1000L * 60 * 60;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    // 1. 토큰 생성
    public String createToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, email);
        claims.put("role", role);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. 토큰에서 이메일(Subject) 추출
    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 3. 토큰 유효성 및 만료일자 확인
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}