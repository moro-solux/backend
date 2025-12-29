package com.example.moro.app.member.service;

import com.example.moro.app.colormap.repository.UserColorMapRepository;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.follow.repository.FollowRepository;
import com.example.moro.app.member.dto.MemberSearchResponse;
import com.example.moro.app.member.dto.ProfileResponse;
import com.example.moro.app.member.dto.UserColor;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.post.repository.PostRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 회원 관련 비즈니스 로직 처리하는 서비스
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserColorMapRepository userColorMapRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;

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
     * 이메일로 회원 찾기 (Optional 반환)
     * @param email 회원 이메일
     * @return 회원 엔티티 Optional
     */
    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
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

    @Transactional(readOnly = true)
    public Page<MemberSearchResponse> search(String keyword, Pageable pageable){
        return memberRepository.findByUserNameContaining(keyword, pageable)
                .map(MemberSearchResponse::from);
    }

    public ProfileResponse getProfile(Long targetUserId, Long currentUserId) {
        Member member = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "해당 회원을 찾을 수 없습니다. userId: " + targetUserId));

        String userColorHex = member.getUserColorHex();

        boolean isCurrentUser = member.getId().equals(currentUserId);
        boolean isVisible = isCurrentUser;

        if (!isCurrentUser) {
            isVisible = followRepository
                    .findByFollowerIdAndFollowingId(currentUserId, targetUserId)
                    .map(f -> f.getStatus() == FollowStatus.ACCEPTED)
                    .orElse(false);
        }

        int colorCount = userColorMapRepository.countByMemberAndUnlockedTrue(member);

        int postCount = postRepository.countByMemberId(member.getId());

        int followingCount = followRepository.countByFollowerId(member.getId());

        List<UserColor> colorCodes = Collections.emptyList();
        if (isVisible) {
            colorCodes = userColorMapRepository
                    .findByMemberAndIsRepresentativeTrue(member)
                    .stream()
                    .map(ucm -> new UserColor(
                            ucm.getColorMap().getColorId(),
                            ucm.getColorMap().getHexCode()
                    ))
                    .limit(6)
                    .collect(Collectors.toList());
        }

        return ProfileResponse.builder()
                .userId(member.getId())
                .userName(member.getUserName())
                .userColorHex(userColorHex)
                .colorCount(colorCount)
                .postCount(postCount)
                .followingCount(followingCount)
                .isCurrentUser(member.getId().equals(currentUserId))
                .isVisible(isVisible)
                .colorCodes(colorCodes)
                .build();
    }

}
