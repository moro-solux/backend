package com.example.moro.app.colormap.service;

import com.example.moro.app.colormap.entity.ColorMap;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.colormap.repository.ColorMapRepository;
import com.example.moro.app.colormap.repository.UserColorMapRepository;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserColorMapService {

    private final ColorMapRepository colorMapRepository;
    private final UserColorMapRepository userColorMapRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void ensureUserColorMaps(Member member) {
        List<ColorMap> allColors = colorMapRepository.findAll();
        System.out.println("ColorMap size = " + allColors.size());

        for (ColorMap color : allColors) {
            if (!userColorMapRepository.existsByMemberAndColorMap(member, color)) {
                UserColorMap ucm = new UserColorMap();
                ucm.setMember(member);
                ucm.setColorMap(color);
                ucm.setUnlocked(false);
                ucm.setPostCount(0);
                ucm.setIsRepresentative(false);
                userColorMapRepository.save(ucm);
            }
        }
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
            System.out.println("Requested colorIds: " + colorIds);
            System.out.println("Found UserColorMaps: " + maps.stream().map(m -> m.getColorMap().getColorId()).toList());

            throw new BusinessException( ErrorCode.BAD_REQUEST, "해금되지 않았거나 존재하지 않는 색상이 포함되어 있습니다.");
        }

        for(UserColorMap map : maps){
            map.setIsRepresentative(true);
        }

    }

}
