package com.example.moro.app.member.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원이 없으면 가입시키고, 있으면 가져오는 기능
    @Transactional
    public Member findOrCreateMember(String email, String name) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .email(email)
                                .userName(name)
                                .role(Member.Role.USER)
                                .build()
                ));
    }
}
