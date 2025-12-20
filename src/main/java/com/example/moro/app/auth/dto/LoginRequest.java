package com.example.moro.app.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 시 프론트에서 전달하는 데이터를 담는 객체
 */

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String email; //구글 인증 결과로 받은 사용자의 이메일 주소
    private String name; //구글 인증 결과로 받은 사용자의 전체 이름이나 닉네임
}