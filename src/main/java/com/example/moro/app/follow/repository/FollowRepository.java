package com.example.moro.app.follow.repository;

import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(Member follower, Member followingId);
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Follow> findByFollowerIdAndStatus(Long followerId, FollowStatus status);
    List<Follow> findByFollowingIdAndStatus(Long followindId,FollowStatus status);

    Page<Follow> findByFollowerIdAndStatus(Long followerId, FollowStatus status, Pageable pageable);
    Page<Follow> findByFollowingIdAndStatus(Long followingId, FollowStatus status, Pageable pageable);

    int countByFollowingIdAndStatus(Long followingId, FollowStatus status);
    int countByFollowerIdAndStatus(Long followerId, FollowStatus status);

    @Query("""
        select f
        from Follow f
        where f.following.id = :userId and f.follower.userName like concat('%', :keyword, '%')
        """)
    Page<Follow> searchFollowers(@Param("userId")Long userId,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    @Query("""
        select f
        from Follow f
        where f.follower.id = :userId and f.following.userName like concat('%', :keyword, '%')
    """)
    Page<Follow> searchFollowings(@Param("userId") Long userId,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);


}
