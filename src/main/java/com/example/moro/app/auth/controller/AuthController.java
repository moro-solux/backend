package com.example.moro.app.auth.controller;

import com.example.moro.app.auth.dto.LoginResponse;
import com.example.moro.app.auth.dto.NicknameCheckResponse;
import com.example.moro.app.auth.service.AuthService;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.service.MemberService;
import com.example.moro.global.common.ApiResponseTemplate;
import com.example.moro.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 인증 및 권한 부여를 담당하는 컨트롤러
 * 소셜 로그인 관련 API를 처리함
 */

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    @Operation(
            summary = "구글 로그인",
            description = "구글 OAuth2 로그인을 시작합니다. 이 엔드포인트는 구글 로그인 페이지로 리다이렉트됩니다."
    )
    @GetMapping("/api/login/google")
    public RedirectView loginGoogle() {
        // 구글 OAuth2 인증 페이지로 리다이렉트
        return new RedirectView("/oauth2/authorization/google");
    }

    /*
    @Operation(
            summary = "로그아웃",
            description = "사용자를 로그아웃 처리합니다. (JWT 토큰은 클라이언트에서 삭제 권장)"
    )
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseTemplate<String>> logout() {
        // 클라이언트에서 토큰을 삭제하는 것을 권장
        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, "로그아웃되었습니다.");
    }
*/
    @Operation(
            summary = "닉네임 중복 확인",
            description = "회원가입 시 닉네임의 중복 여부를 확인합니다."
    )
    @GetMapping("/api/check-nickname")
    public ResponseEntity<ApiResponseTemplate<NicknameCheckResponse>> checkNickname(
            @RequestParam String userName) {

        boolean exists = memberService.existsByUserName(userName);

        NicknameCheckResponse response = NicknameCheckResponse.builder()
                .available(!exists)  // 사용 가능 여부
                .exists(exists)      // 중복 여부
                .build();

        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, response);
    }


    @Operation(
            summary = "회원가입 완료",
            description = "OAuth2 로그인 후 이름 설정을 완료하고 최종 회원가입을 처리합니다."
    )
    @PostMapping("/api/complete-registration")
    public ResponseEntity<ApiResponseTemplate<LoginResponse>> completeRegistration(
            @RequestParam String email,
            @RequestParam String userName,
            //위치 측정 동의 여부
            @RequestParam(defaultValue = "true") Boolean locationConsent) {

        LoginResponse response = authService.completeRegistration(email, userName,locationConsent);
        return ApiResponseTemplate.success(SuccessCode.RESOURCE_CREATED, response);
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 계정을 삭제합니다."
    )
    @DeleteMapping("/api/cancel")
    public ResponseEntity<ApiResponseTemplate<String>> cancel(Authentication authentication) {
        // 현재 로그인한 사용자의 이메일을 가져옴
        String email = authentication.getName();

        // 이메일로 회원 찾기
        Member member = memberService.findByEmail(email);

        // 회원 삭제
        memberService.deleteMember(member.getId());

        return ApiResponseTemplate.success(SuccessCode.OPERATION_SUCCESSFUL, "회원 탈퇴가 완료되었습니다.");
    }
}