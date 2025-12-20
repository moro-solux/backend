package com.example.moro.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 모든 요청에서 jwt 토큰의 유효성을 검사하는 필터
 * OncePerRequestFilter를 상속받아 요청당 한번말 실행됨을 보장
 */

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // [Step 1] HTTP 요청 헤더에서 JWT 토큰을 추출
        String token = resolveToken(request);

        // [Step 2] 토큰이 존재하고 유효성 검증을 통과하면 인증 처리
        if (token != null && jwtProvider.validateToken(token)) {
            String email = jwtProvider.getEmail(token);

            // 스프링 시큐리티 내부에서 사용할 인증 객체(Authentication) 생성
            // 현재 프로젝트는 단순화를 위해 권한 목록(Authorities)은 빈 리스트로 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());

            // [Step 3] 생성한 인증 객체를 SecurityContext에 저장
            // 이렇게 저장해두면 컨트롤러 등에서 @AuthenticationPrincipal로 정보를 꺼낼 수 있음
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // [Step 4] 다음 보안 필터 또는 서블릿으로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP Header에서 'Authorization: Bearer [Token]' 형식의 값을 찾아 토큰만 반환함
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}