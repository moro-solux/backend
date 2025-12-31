package com.example.moro.app.colormap.repository;

import com.example.moro.app.colormap.dto.ColorDetailResponse;
import com.example.moro.app.colormap.entity.ColorMap;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.colormap.entity.UserColorMapId;
import com.example.moro.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public interface UserColorMapRepository extends JpaRepository<UserColorMap, UserColorMapId> {

    boolean existsByMemberAndColorMap(Member member, ColorMap colorMap);
    int countByMemberAndUnlockedTrue(Member member);
    List<UserColorMap> findByMemberAndIsRepresentativeTrue(Member member);

    Optional<UserColorMap> findByMemberAndColorMapColorId(Member member, Long colorId);

    Optional<UserColorMap> findByMemberIdAndColorMapColorId(Long memberId, Long colorId);

    // 2. 대표색 초기화
    @Modifying
    @Query("update UserColorMap u set u.isRepresentative = false\n" +
            "    where u.member = :member\n" +
            "      and u.isRepresentative = true")
    void clearRepresentativeByMember(@Param("member") Member member);

    // 기존 유지 -> 사용자가 선택한 특정 색만 검증/처리
    List<UserColorMap> findByMemberAndUnlockedIsTrueAndColorMap_ColorIdIn(Member member, List<Long> colorIds);

    // 3. 통합 조회 + 런타임 에러 해결
    @Query("SELECT ucm FROM UserColorMap ucm JOIN FETCH ucm.colorMap WHERE ucm.member= :member")
    List<UserColorMap> findAllByMemberWithColorMap(@Param("member") Member member);


    // 테마별 색 조회에 사용
    @Query("SELECT ucm FROM UserColorMap ucm " +
            "JOIN FETCH ucm.colorMap " +
            "WHERE ucm.member = :member AND ucm.colorMap.colorTheme = :theme")
    List<UserColorMap> findAllByMemberAndTheme(@Param("member") Member member, @Param("theme") String theme);

}
