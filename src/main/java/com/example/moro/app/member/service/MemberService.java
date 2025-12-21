package com.example.moro.app.member.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원 관련 비즈니스 로직 처리하는 서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;


    /**
     * 소셜 로그인 사용자의 회원가입 여부를 확인하고 처리
     * 기존 회원이면 조회 정보를 반환하고, 신규 회원이면 db에 저장 후 반환함.
     * @param email 사용자 식별 이메일
     * @param name 사용자 이름 (닉네임)
     * @return 가입 혹은 조회된 member 엔티티
     */

    @Transactional //데이터 저장이 발생하여 쓰기 권한 허용
    public Member findOrCreateMember(String email, String name) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 신규 회원일 경우 빌더 패턴을 통해 엔티티 생성 및 저장
                    return memberRepository.save(
                        Member.builder()
                                .email(email)
                                .userName(name)
                                .role(Member.Role.USER) //기본 권한 user 부여
                                .build()
                        );
                });
    }

    /**
     * 이메일로 회원 찾기
     * @param email 회원 이메일
     * @return 회원 엔티티
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    }

    /**
     * 회원 탈퇴 처리
     * @param memberId 탈퇴할 회원 ID
     */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        memberRepository.delete(member);
    }
}
