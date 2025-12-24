package com.example.moro.app.follow;

import com.example.moro.app.follow.dto.FollowResponseDto;
import com.example.moro.app.follow.dto.FollowUserResponse;
import com.example.moro.app.follow.entity.Follow;
import com.example.moro.app.follow.entity.FollowStatus;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;


    public Follow requestFollow(Long followerId, Long followingId){

        if(followerId.equals(followingId)){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "자기자신은 팔로우 할 수 없습니다.");
        }
        Member follower = memberRepository.findById(followerId).orElseThrow();
        Member following = memberRepository.findById(followingId).orElseThrow();
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BusinessException(ErrorCode. ALREADY_EXIST_SUBJECT_EXCEPTION, "이미 팔로우 중입니다.");
        }

        Follow follow = Follow.create(follower, following);
        followRepository.save(follow);

        return follow;
    }

    public void removeByFollower(Long myUserId, Long targetUserId) {
        Follow follow = followRepository.findByFollowerIdAndFollowingId(myUserId, targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "팔로우 관계가 존재하지 않습니다."));

        if(myUserId.equals(targetUserId)){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "자기자신과의 팔로우 관계 생성 및 삭제는 불가능합니다..");
        }
            followRepository.delete(follow);
    }
    @Transactional
    public FollowResponseDto approveFollow(Long followId, Long myUserId) {

        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "팔로우 요청이 존재하지 않습니다."
                ));

        if (!follow.getFollowing().getId().equals(myUserId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        if (follow.getStatus() != FollowStatus.PENDING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "승인할 수 없는 상태입니다.");
        }

        follow.accept();
        followRepository.save(follow);

        return new FollowResponseDto(
                follow.getFollowId(),
                follow.getStatus()
        );
    }

    public void rejectByFollowing(Long followId, Long myUserId) {
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND, "팔로우 요청이 존재하지 않습니다."
                ));

        if (!follow.getFollowing().getId().equals(myUserId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED_EXCEPTION);
        }

        if (follow.getStatus() != FollowStatus.PENDING) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "거절할 수 없는 상태입니다.");
        }

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public List<FollowUserResponse>  getFollowerList(Long myUserId){
        return followRepository
                .findByFollowingIdAndStatus(myUserId, FollowStatus.ACCEPTED)
                .stream()
                .map(FollowUserResponse::fromFollower)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowingList(Long myUserId) {
        return followRepository
                .findByFollowerIdAndStatus(myUserId, FollowStatus.ACCEPTED)
                .stream()
                .map(FollowUserResponse::fromFollowing)
                .collect(Collectors.toList());
    }



}
