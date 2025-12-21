package com.example.moro.app.follow;

import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(Member follower, Member followingId);

    List<Follow> findByFollowingAndStatus(Member following, FollowStatus status);
    List<Follow> findByFollowerAndStatus(Member follower, FollowStatus status);

}
