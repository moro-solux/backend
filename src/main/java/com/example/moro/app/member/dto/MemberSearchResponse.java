package com.example.moro.app.member.dto;

import com.example.moro.app.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSearchResponse {
    private Long userId;
    private String userName;

    public static MemberSearchResponse from(Member member){
        return new MemberSearchResponse(
                member.getId(),
                member.getUserName()
        );
    }
}
