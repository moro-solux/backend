package com.example.moro.app.mission.service;

import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.app.mission.entity.Mission;
import com.example.moro.app.mission.repository.MissionRepository;
import com.example.moro.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionNotificationScheduler {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final MissionRepository missionRepository;

    //@Scheduled(cron = "*/1 * * * * *") // 매 1초마다 실행
    @Scheduled(cron = "0 0 11,23 * * *", zone= "Asia/Seoul") // 오전 11시와 오후 11시에만 실행
    public void sendMissionEndNotifications() {

        List<Mission> missions = missionRepository.findMissionsEndingInOneHour();

        for (Mission mission : missions) {
            List<Member> targetMembers = memberRepository.findNonParticipants(mission);

            targetMembers.forEach(member ->
                    notificationService.notifyMission(
                            member.getId(),
                            mission.getMissionId(),
                            mission.getMissionTitle(),
                            1
                    )
            );
        }
    }
}
