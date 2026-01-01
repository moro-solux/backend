package com.example.moro.app.post.dto;

import lombok.Getter;

//게시물 공유 응답
@Getter
public class ShareResponse {
    private String shareUrl;

    public ShareResponse(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
