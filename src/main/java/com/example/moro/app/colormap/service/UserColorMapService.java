package com.example.moro.app.colormap.service;

import com.example.moro.app.colormap.entity.ColorMap;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.colormap.repository.ColorMapRepository;
import com.example.moro.app.colormap.repository.UserColorMapRepository;
import com.example.moro.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserColorMapService {

    private final ColorMapRepository colorMapRepository;
    private final UserColorMapRepository userColorMapRepository;

    @Transactional
    public void ensureUserColorMaps(Member member) {
        List<ColorMap> allColors = colorMapRepository.findAll();
        System.out.println("ColorMap size = " + allColors.size());

        for (ColorMap color : allColors) {
            if (!userColorMapRepository.existsByMemberAndColorMap(member, color)) {
                UserColorMap ucm = new UserColorMap();
                ucm.setMember(member);
                ucm.setColorMap(color);
                ucm.setUnlocked(false);
                ucm.setPostCount(0);
                ucm.setIsRepresentative(false);
                userColorMapRepository.save(ucm);
            }
        }
    }

}
