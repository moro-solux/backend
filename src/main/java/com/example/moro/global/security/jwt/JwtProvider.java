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

/**
 * jwt 생성 및 유효성 검증을 담당하는 컴포넌트
 */
@Component
public class JwtProvider {

    // 설정파일(yml)의 암호화 키
    @Value("${jwt.secret}")
    private String salt;

    private Key secretKey;

    // 토큰 유효시간: 1시간
    private final long exp = 1000L * 60 * 60;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 사용자 정보를 바탕으로 JWT 액세스 토큰 생성
     * @param email 사용자 식별자(Subject)
     * @param role 사용자 권한
     * @return 생성된 JWT 문자열
     */

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

    /**
     * 토큰 본문(Claims)에서 사용자 이메일 추출
     * @param token 검증할 JWT 토큰
     * @return 사용자 이메일
     */

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 토큰의 유효성 및 만료 여부 확인
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 변조되었거나 만료되면 false
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}