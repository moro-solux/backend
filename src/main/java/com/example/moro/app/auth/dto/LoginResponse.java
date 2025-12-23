package com.example.moro.app.auth.dto;

import com.example.moro.app.member.dto.MemberResponse;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 성공 시 클라이언트에게 반환되는 최종 응답 객체
 */

@Getter
@Builder
public class LoginResponse {
    private MemberResponse member; //로그인한 사용자의 정보
    private String token; //jwt 토큰
    private boolean needsNameSetup; // 이름 설정이 필요한지 여부
    private String tempEmail; // 임시 이메일 (이름 설정 시 사용)
}

