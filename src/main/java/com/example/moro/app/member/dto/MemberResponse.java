package com.example.moro.app.member.dto;

import com.example.moro.app.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원 정보 응답 전용 데이터 객체 (DTO)
 * API 응답시 member 엔티티의 모든 정보를 노출하지 않고,
 * 클라이언트에 필요한 정보만 선별하여 전달하는 역할
 */

@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;

    /**
     * Member 엔티티를 memberResponse DTO로 변환
     * 서비스 내부 로직과 외부 응답 규격을 분리하기 위해 사용
     * @param member 변환할 회원 엔티티 객체
     * @return 화면 전달용으로 가공된 MemberRespnse 객체
     */
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getUserName())
                .build();
    }
}
