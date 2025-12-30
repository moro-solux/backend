package com.example.moro.app.colormap.repository;

import com.example.moro.app.colormap.entity.ColorMap;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserColorMapRepository extends JpaRepository<UserColorMap, Long> {
    boolean existsByMemberAndColorMap(Member member, ColorMap colorMap);

    int countByMemberAndUnlockedTrue(Member member);

    List<UserColorMap> findByMemberAndIsRepresentativeTrue(Member member);

    Optional<UserColorMap> findByMemberIdAndColorMapColorId(Long memberId, Long colorId);

    @Modifying
    @Query("""
    update UserColorMap u
    set u.isRepresentative = false
    where u.member = :member
      and u.isRepresentative = true
    """)
    void clearRepresentativeByMember(@Param("member") Member member);

    List<UserColorMap> findByMemberAndUnlockedIsTrueAndColorMap_ColorIdIn(Member member, List<Long> colorIds);



}
