package com.example.moro.app.colormap.entity;

import com.example.moro.app.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@IdClass(UserColorMapId.class) // 식별자 class와 연결
public class UserColorMap {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "color_id")
    private ColorMap colorMap;

    private Integer postCount;   // 게시물 개수
    private Boolean unlocked;   // True = 해금, False = 미해금
    private Boolean isRepresentative;   // 대표색 여부
}
