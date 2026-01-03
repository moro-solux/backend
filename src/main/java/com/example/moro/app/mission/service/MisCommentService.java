package com.example.moro.app.mission.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.mission.dto.MisCommentRequest;
import com.example.moro.app.mission.dto.MisCommentResponse;
import com.example.moro.app.mission.entity.MisComment;
import com.example.moro.app.mission.entity.MissionPost;
import com.example.moro.app.mission.repository.MisCommentRepository;
import com.example.moro.app.mission.repository.MissionPostRepository;
import com.example.moro.app.mission.repository.MissionRepository;
import com.example.moro.global.common.ErrorCode;
import com.example.moro.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MisCommentService {

    private final MissionPostRepository missionPostRepository;
    private final MemberRepository memberRepository;
    private final MisCommentRepository misCommentRepository;

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<MisCommentResponse> getMisComments(Long misPostId){

        // 모든 댓글 조회
        return misCommentRepository.findByMissionPost_MisPostIdOrderByMisCreatedAtAsc(misPostId)
                .stream()
                .map(comment -> new MisCommentResponse(
                        comment.getMisCommentId(),
                        comment.getMisContent(),
                        comment.getMissionPost().getMember().getUserName(),
                        comment.getMisCreatedAt()
                ))
                .toList();
    }

    // 댓글 생성
    @Transactional
    public Long createMisComments (String email, MisCommentRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(()-> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"사용자를 찾을 수 없습니다."));

        MissionPost post = missionPostRepository.findById(request.misPostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"미션 게시글을 찾을 수 없습니다."));

        MisComment comment = MisComment.builder()
                .missionPost(post)
                .member(member)
                .misContent(request.misContent())
                .misCreatedAt(LocalDateTime.now())
                .build();

        return misCommentRepository.save(comment).getMisCommentId();
    }

    // 댓글 수정
    @Transactional
    public void updateMisComments(String email, Long misCommentId, String newContent){
        MisComment comment = misCommentRepository.findById(misCommentId)
                .orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 본인 확인
        if(!comment.getMember().getEmail().equals(email)) {
            throw new RuntimeException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }
        comment.updateContent(newContent);
    }

    // 댓글 삭제
    @Transactional
    public void deleteMisComments(String email, Long misCommentId){
        MisComment comment = misCommentRepository.findById(misCommentId)
                .orElseThrow(()-> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 본인 확인
        if(!comment.getMember().getEmail().equals(email)) {
            throw new RuntimeException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        misCommentRepository.delete(comment);
    }
}
