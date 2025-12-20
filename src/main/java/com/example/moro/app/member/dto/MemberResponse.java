package com.example.moro.app.member.dto;

import com.example.moro.app.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getUserName())
                .build();
    }
}
