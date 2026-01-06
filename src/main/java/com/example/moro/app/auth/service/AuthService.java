package com.example.moro.app.auth.service;

import com.example.moro.app.auth.dto.LoginResponse;
import com.example.moro.app.colormap.service.UserColorMapService;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 인증 관련 핵심 비즈니스 로직을 담당하는 서비스
 * 로그인 처리 및 jwt 토큰 발급을 주관함
 */

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final UserColorMapService userColorMapService;
    /**
     * 구글 인증 정보를 기잔으로 서비스 로그인을 수행
     * 1. 기존 회원 여부 확인 및 자동 가입
     * 2. 회원 정보를 바탕으로 자체 서비스 전용 jwt 생성
     * 3. 토큰과 함께 회원 정보를 클라이언트에 반환
     * * @param email 구글로부터 전달받은 사용자 이메일
     * @param name  구글로부터 전달받은 사용자 이름 (필요한가????)
     * return 발급된 토큰과 회원 정보가 포함된 LoginResponse
     */
    public LoginResponse login(String email, String name) {
        //[step1] 구글 정보 바탕으로 회원 확인 및 회원가입 처리
        Member member = memberService.findOrCreateMember(email, name);

        // [step2] 회원 식별값(email)과 권한을 사용해서 jwt 토큰을 생성
        String token = jwtProvider.createToken(member.getEmail(), member.getRole().name());

        //[step3] 최종 응답 객체 생성 및 반환
        return LoginResponse.builder()
                .member(com.example.moro.app.member.dto.MemberResponse.from(member))
                .token(token)
                .build();
    }

    /**
     * OAuth2 로그인 처리 (이름 없이 이메일만으로)
     * 기존 회원이면 바로 로그인, 신규 회원이면 이름 설정 필요 표시
     * @param email 구글에서 받은 이메일
     * @return LoginResponse
     */
    public LoginResponse handleOAuthLogin(String email) {
        // 이메일로 기존 회원 조회
        Optional<Member> existingMember = memberService.findMemberByEmail(email);

        if (existingMember.isPresent()) {
            // 기존 회원: 바로 로그인 처리
            Member member = existingMember.get();
            String token = jwtProvider.createToken(member.getEmail(), member.getRole().name());

            return LoginResponse.builder()
                    .member(com.example.moro.app.member.dto.MemberResponse.from(member))
                    .token(token)
                    .needsNameSetup(false)
                    .build();
        } else {
            // 신규 회원: 이름 설정 필요
            return LoginResponse.builder()
                    .needsNameSetup(true)
                    .tempEmail(email)
                    .build();
        }
    }

    /**
     * 회원가입 완료 처리 (이름 설정 후 최종 회원가입)
     * @param email 사용자의 이메일
     * @param userName 사용자가 설정한 이름
     * @return LoginResponse
     */
    public LoginResponse completeRegistration(String email, String userName) {
        // 이메일과 이름으로 최종 회원가입
        Member member = memberService.findOrCreateMember(email, userName);

        userColorMapService.ensureUserColorMaps(member);

        // JWT 토큰 발급
        String token = jwtProvider.createToken(member.getEmail(), member.getRole().name());

        return LoginResponse.builder()
                .member(com.example.moro.app.member.dto.MemberResponse.from(member))
                .token(token)
                .needsNameSetup(false)
                .build();
    }
}
