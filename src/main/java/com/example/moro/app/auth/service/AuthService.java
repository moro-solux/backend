package com.example.moro.app.auth.service;

import com.example.moro.app.auth.dto.LoginResponse;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public LoginResponse login(String email, String name) {
        // 구글 정보를 바탕으로 회원 확인 및 생성
        Member member = memberService.findOrCreateMember(email, name);

        // JWT 토큰 생성
        String token = jwtProvider.createToken(member.getEmail(), member.getRole().name());

        return LoginResponse.builder()
                .member(com.example.moro.app.member.dto.MemberResponse.from(member))
                .token(token)
                .build();
    }
}
