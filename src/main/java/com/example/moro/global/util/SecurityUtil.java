package com.example.moro.global.util;

import com.example.moro.app.member.entity.Member;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Member) {
            return (Member) principal;
        }

        throw new BusinessException(ErrorCode.UNAUTHORIZED_EXCEPTION, "알 수 없는 principal 타입: " + principal.getClass());
    }
}