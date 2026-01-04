package com.example.moro.app.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notiId;

    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private NotificationType type;

    @Column(columnDefinition = "json")
    private String content;

    @Column(name= "isRead")
    private boolean read;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        read = false;
    }

    public static Notification create(Long receiverId, NotificationType type) {
        return Notification.builder()
                .receiverId(receiverId)
                .type(type)
                .build();
    }

    public static Notification create(Long receiverId, NotificationType type, String content) {
        return Notification.builder()
                .receiverId(receiverId)
                .type(type)
                .content(content)
                .build();
    }


}
