package com.example.moro.app.member.service;

import com.example.moro.app.member.dto.UserNotificationStatus;
import com.example.moro.app.member.dto.UserPublicStatus;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberSettingService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public UserNotificationStatus viewNotificationStatus(Member member) {
        Member foundmember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return new UserNotificationStatus(foundmember.getIsNotification());
    }

    @Transactional(readOnly = true)
    public UserPublicStatus viewPublicStatus(Member member) {
        Member foundmember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return new UserPublicStatus(member.getIsPublic());
    }

    @Transactional
    public void updateNotification(Member member, Boolean isNotification){
        Member foundmember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        foundmember.setIsNotification(isNotification);
    }

    @Transactional
    public void updatePublic(Member member, Boolean isPublic){
        Member foundmember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        foundmember.setIsPublic(isPublic);
    }
}
