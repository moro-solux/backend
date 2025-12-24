package com.example.moro.app.follow;

import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(Member follower, Member followingId);
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    List<Follow> findByFollowerIdAndStatus(Long followerId, FollowStatus status);
    List<Follow> findByFollowingIdAndStatus(Long followindId,FollowStatus status);

}
