package com.example.moro.app.member.service;

import com.example.moro.app.colormap.entity.ColorMap;
import com.example.moro.app.colormap.repository.ColorMapRepository;
import com.example.moro.app.colormap.repository.UserColorMapRepository;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.follow.repository.FollowRepository;
import com.example.moro.app.member.dto.*;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.post.entity.Post;
import com.example.moro.app.post.repository.PostRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.common.dto.PageResponse;
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
    private final ColorMapRepository colorMapRepository;
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

    @Transactional
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
        boolean isVisible = isCurrentUser || member.getIsPublic();

        if(!isVisible) {
            isVisible = followRepository
                    .findByFollowerIdAndFollowingId(currentUserId, targetUserId)
                    .map(f -> f.getStatus() == FollowStatus.ACCEPTED)
                    .orElse(false);
        }

        int colorCount = userColorMapRepository.countByMemberAndUnlockedTrue(member);

        int postCount = postRepository.countByMemberId(member.getId());

        int followingCount = followRepository.countByFollowerId(member.getId());

        List<UserColor> colorCodes = Collections.emptyList();
        if(isVisible) {
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

    @Transactional
    public void updateProfile(Long memberId, String userName, Long userColorId, String userColorHex) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 회원을 찾을 수 없습니다. userId: " + memberId));

        if (userName != null && !userName.isBlank()) {
            member.setUserName(userName);
        }
        if (userColorId != null && userColorHex != null
                && !userColorHex.isBlank()) {

            ColorMap colorMap = colorMapRepository.findById(userColorId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "해당 색상 ID가 존재하지 않습니다(1~144). colorId: " + userColorId));

            if (!colorMap.getHexCode().equalsIgnoreCase(userColorHex)) {
                throw new BusinessException(ErrorCode.BAD_REQUEST,
                        "colorId와 colorHex가 일치하지 않습니다. colorId: " + userColorId + ", colorHex: " + userColorHex);
            }

            UserColorMap userColorMap = userColorMapRepository
                    .findByMemberIdAndColorMapColorId(memberId, userColorId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "사용자가 해당 색상을 해금하지 않았습니다. colorId: " + userColorId));

            if (!Boolean.TRUE.equals(userColorMap.getUnlocked())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST,
                        "사용자가 해당 색상을 아직 해금하지 않았습니다. colorId: " + userColorId);
            }

            member.setUserColorId(userColorId);
            member.setUserColorHex(userColorHex);
        }
        memberRepository.save(member);
    }


    public UserFeedListResponse getProfileFeed(Long targetUserId, ProfileFeedType type, Integer colorId, Pageable pageable) {
        Member member = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException( ErrorCode.RESOURCE_NOT_FOUND, "해당 회원이 존재하지 않습니다."));

        Page<Post> postPage;

        switch(type) {
            case SINGLE_COLOR -> {
                if (colorId == null) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST, "colorId는 필수입니다.");
                }
                postPage = postRepository.findByMemberAndMainColorIdOrderByCreatedAtDesc( member, colorId, pageable);
            }

            case DEFAULT -> {
                if (member.getUserColorId() == null) {
                    postPage = Page.empty(pageable);
                    break;
                }
                Integer userColorId = member.getUserColorId().intValue();
                postPage = postRepository.findByMemberAndMainColorIdOrderByCreatedAtDesc( member, userColorId, pageable);
            }

            case USER_COLORS -> {
                List<Integer> representativeColorIds =
                        userColorMapRepository
                                .findByMemberAndIsRepresentativeTrue(member)
                                .stream()
                                .map(ucm -> ucm.getColorMap().getColorId().intValue())
                                .toList();

                if (representativeColorIds.isEmpty()) {
                    postPage = Page.empty(pageable);
                    break;
                }

                postPage = postRepository.findByMemberAndMainColorIdInOrderByCreatedAtDesc(member, representativeColorIds, pageable);
            }

            default -> throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 조회 타입입니다. viewType은 DEAFULT / SINGLE_COLOR / USER_COLORS 중 하나여야 합니다.");
        }

        PageResponse<UserFeedResponse> pageResponse = PageResponse.from(postPage.map(this::toResponse));


        return UserFeedListResponse.builder()
                .viewType(type)
                .page(pageResponse)
                .build();
    }

    private UserFeedResponse toResponse(Post post) {
        return UserFeedResponse.builder()
                .postId(post.getId())
                .imageUrl(post.getImageUrl())
                .build();
    }

    @Transactional
    public void updateRepresentativeColors(Long memberId, List<Long> colorIds) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "회원이 존재하지 않습니다."
                ));

        userColorMapRepository.clearRepresentativeByMember(member);

        if(colorIds == null || colorIds.isEmpty()) {
            return;
        }

        if(colorIds.size() > 6) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "대표 색상은 최대 6개까지 선택할 수 있습니다.");
        }

        boolean invalidRange = colorIds.stream().anyMatch(id -> id < 1 || id > 144);
        if(invalidRange) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "colorId는 1~144 범위 내의 값이어야 합니다.");
        }


        List<UserColorMap> maps = userColorMapRepository.findByMemberAndUnlockedIsTrueAndColorMap_ColorIdIn(member, colorIds);
        if(maps.size() != colorIds.size()) {
            throw new BusinessException( ErrorCode.BAD_REQUEST, "해금되지 않았거나 존재하지 않는 색상이 포함되어 있습니다.");
        }

        for(UserColorMap map : maps){
            map.setIsRepresentative(true);
        }

    }


}

