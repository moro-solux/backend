package com.example.moro.global.util;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final MemberRepository memberRepository;

    public SecurityUtil(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Member) {
            return (Member) principal;
        }

        if (principal instanceof OidcUser oidcUser) {
            String email = oidcUser.getEmail();
            return memberRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION));
        }

        throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "알 수 없는 principal 타입: " + principal.getClass());
    }
}