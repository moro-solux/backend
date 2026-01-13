package com.example.moro.app.post.dto;

import com.example.moro.app.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id; //게시물 id
    private String imageUrl; //게시물 이미지 url
    private String userName; //사용자 아이디
    private String userProfileImageUrl; //사용자 프로필 이미지 url (임시: 대표 색상)
    private LocalDateTime createdAt; //게시물 생성 시간
    private String likecount; //좋아요 개수
    private int shareCount; //공유 횟수
    private int commentCount; //댓글 개수
    private Double lat; //위도
    private Double lng; //경도
    private String placeName; //주소 정보
    private List<ColorInfo> colors; //게시물 색상 정보 (컬러id, hexcode, 비율)

    // 색상 정보를 담는 내부 클래스
    @Getter
    public static class ColorInfo {
        private Integer colorId;
        private String hexCode; // String으로 수정
        private Double ratio;

        public ColorInfo(Integer colorId, String hexCode, Double ratio) {
            this.colorId = colorId;
            this.hexCode = hexCode;
            this.ratio = ratio;
        }
    }

    // 확장된 생성자 (모든 필드 포함)
    public PostResponseDto(Post post, int likeCount, int commentCount,
                          List<ColorInfo> colors, String userName, String userProfileImageUrl) {
        this.id = post.getId();
        this.imageUrl = post.getImageUrl();
        this.createdAt = post.getCreatedAt();
        this.shareCount = post.getShareCount();
        this.lat = post.getLat();
        this.lng = post.getLng();
        this.placeName = post.getAddress();  // 주소 정보 추가
        this.likecount = String.valueOf(likeCount);
        this.commentCount = commentCount;
        this.colors = colors;
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
    }

}
