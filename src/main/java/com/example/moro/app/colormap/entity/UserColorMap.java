package com.example.moro.app.colormap.entity;

import com.example.moro.app.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class UserColorMap {

    @OneToMany
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private ColorMap colorMap;

    private Integer postCount;   // 게시물 개수
    private Boolean unlocked;   // True = 해금, False = 미해금
    private Boolean isRepresentative;   // 대표색 여부
}
