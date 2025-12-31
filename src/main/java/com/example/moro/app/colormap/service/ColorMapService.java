package com.example.moro.app.colormap.service;

import com.example.moro.app.colormap.dto.ColorDetailResponse;
import com.example.moro.app.colormap.dto.ColorPostResponse;
import com.example.moro.app.colormap.dto.ThemeGroupResponse;
import com.example.moro.app.colormap.dto.UpdateMainColorResponse;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.colormap.repository.UserColorMapRepository;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.post.entity.Post;
import com.example.moro.app.post.repository.PostColorRepository;
import com.example.moro.app.post.repository.PostRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.common.dto.PageResponse;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorMapService {
    private final MemberRepository memberRepository; // Member 조회용
    private final UserColorMapRepository userColorMapRepository;
    private final PostRepository postRepository;
    private final PostColorRepository postColorRepository;

    /*
    테마별 컬러맵 현황 조회 -> 사용자의 컬러맵 내 해금 여부 + 사진 개수 반환
     */
    @Transactional(readOnly = true)
    public List<ThemeGroupResponse> getUserColorMaps(String email) {   // 테마별 컬러 반환
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "회원을 찾을 수 없습니다."));

        //  유저의 ID 가져오기
        Long userId = member.getId();

        // 모든 색상 정보와 사용자의 해금 상태 한 번에 조회
        List<UserColorMap> userColors = userColorMapRepository.findAllByMemberWithColorMap(member);

        // 테마 별로 그룹화해 변환
        return userColors.stream()
                .collect(Collectors.groupingBy(ucm -> ucm.getColorMap().getColorTheme()))
                .entrySet().stream()
                .map(entry -> new ThemeGroupResponse(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(ucm -> new ColorDetailResponse(
                                        ucm.getColorMap().getColorId(),
                                        ucm.getColorMap().getHexCode(),
                                        ucm.getPostCount(),
                                        ucm.getUnlocked(),
                                        ucm.getIsRepresentative()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ThemeGroupResponse getUserColorMapsByTheme(String email, String themeName) {
        // 1. 이메일로 유저 조회 및 ID 획득
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "회원을 찾을 수 없습니다."));

        System.out.println("현재 로그인한 유저의 DB ID: " + member.getId());
        List<UserColorMap> userColors = userColorMapRepository.findAllByMemberAndTheme(member, themeName);

        List<ColorDetailResponse> colorDetails = userColors.stream()
                .map(ucm -> new ColorDetailResponse(
                        ucm.getColorMap().getColorId(),
                        ucm.getColorMap().getHexCode(),
                        ucm.getPostCount(),
                        ucm.getUnlocked(),
                        ucm.getIsRepresentative()
                )).toList();

        return new ThemeGroupResponse(themeName, colorDetails);
    }

    /*
    게시물의 대표색 변경
    */
    /*@Transactional
    public UpdateMainColorResponse updatePostMainColor(Long memberId, Long postId, Long newColorId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "회원을 찾을 수 없습니다."));

        // 1. 게시물 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "게시물을 찾을 수 없습니다."));

        // 본인 게시물인지 검증(추가)
        if(!post.getMember().equals(memberId)){
            throw new BusinessException(ErrorCode.ACCESS_DENIED_EXCEPTION,"수정 권한이 없습니다.");
        }

        // 2. PostColor에서 추출했던 4개 확인
        List<Long> candidates = postColorRepository.findColorIdsByPostId(postId);
        if(!candidates.contains(newColorId)){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "추출된 후보 색상이 아닙니다.");
        }

        // 3. 사진 수 및 해금 처리
        updateUserColorStatus(member, post.getMainColorId().longValue(), -1);  // int -> long 변환
        updateUserColorStatus(member, newColorId, 1);

        // 4. 대표색 변경
        post.setMainColorId(newColorId.intValue());

        // 5. 결과 반환 위한 정보 조회
        UserColorMap updatedUcm = userColorMapRepository.findByMemberAndColorMapColorId(member, newColorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "컬러맵 정보를 찾을 수 없습니다."));

        return new UpdateMainColorResponse(
                post.getId(),
                post.getMainColorId(),
                updatedUcm.getColorMap().getHexCode(),
                updatedUcm.getUnlocked()
        );
    }*/

    /*
    사용자 특정 컬러맵 통계 업데이트 <내부 로직>
     */
    /*private void updateUserColorStatus(Member member, Long colorId, int delta){
        UserColorMap ucm = userColorMapRepository.findByMemberAndColorMapColorId(member,colorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "유저 컬러맵을 찾을 수 없습니다."));

        // postCount 변경 + 0 이하 방지
        int currentCount = ucm.getPostCount() != null ?  ucm.getPostCount() : 0;
        ucm.setPostCount(Math.max(0, currentCount + delta));
        // 사진이 1개라도 등록되면 해금 처리
        if(ucm.getPostCount() > 0) ucm.setUnlocked(true);
    }*/

    /*
    특정 색상의 게시물 조회
     */
    /*public PageResponse<ColorPostResponse> getPostsByColor(Long memberId, Long colorId, Pageable pageable){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "회원을 찾을 수 없습니다."));

        return PageResponse.from(
                postRepository.findByMemberAndMainColorIdOrderByCreatedAtDesc(member, colorId.intValue(), pageable)
                        .map(post -> new ColorPostResponse(post.getId(), post.getImageUrl()))
        );
    }*/

}
