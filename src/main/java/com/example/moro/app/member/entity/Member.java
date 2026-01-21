package com.example.moro.app.member.entity;

import jakarta.persistence.*;
import lombok.*;

import javax.management.relation.Role;

@Entity
@Getter
@Table(name = "user")
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //사용자 id(pk)

    @Column(nullable = false, unique = true)
    private String email; //이메일

    @Setter
    private String userName; //유저 닉네임

    @Setter
    private Long userColorId; //유저 대표 색상 id

    @Setter
    private String userColorHex; //유저 대표색상 코드

    @Setter
    @Builder.Default
    private Boolean isNotification=true; //알림 수신 여부

    @Setter
    @Builder.Default
    private Boolean isPublic=true; // 공개/비공개 여부

    @Setter
    @Builder.Default
    @Column(name = "location_consent")
    private Boolean locationConsent=true;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // USER, ADMIN

    public enum Role {
        USER,
        ADMIN
    }

    public boolean canBeFollowedDirectly() {
        return isPublic;
    }

}
