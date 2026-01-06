package com.example.moro.app.colormap;

import com.example.moro.app.colormap.entity.ColorMap;
import com.example.moro.app.colormap.entity.UserColorMap;
import com.example.moro.app.colormap.repository.ColorMapRepository;
import com.example.moro.app.colormap.repository.UserColorMapRepository;
import com.example.moro.app.member.entity.Member;
import com.example.moro.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ColorMapInitializer implements CommandLineRunner {

    private final ColorMapRepository colorMapRepository;
    private final UserColorMapRepository userColorMapRepository;
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        if(colorMapRepository.count() == 0) {
            List<ColorMap> colors = List.of(
                    new ColorMap(null, "pastel", "F5D8E0"),
                    new ColorMap(null, "pastel", "EEB6C6"),
                    new ColorMap(null, "pastel", "E895AB"),
                    new ColorMap(null, "pastel", "DF7DA8"),
                    new ColorMap(null, "pastel", "CD5F80"),
                    new ColorMap(null, "pastel", "B34166"),
                    new ColorMap(null, "pastel", "F7E6BA"),
                    new ColorMap(null, "pastel", "F3D6A5"),
                    new ColorMap(null, "pastel", "EEBB84"),
                    new ColorMap(null, "pastel", "E99F65"),
                    new ColorMap(null, "pastel", "D1814B"),
                    new ColorMap(null, "pastel", "B76B40"),
                    new ColorMap(null, "pastel", "FCF9CA"),
                    new ColorMap(null, "pastel", "FAF5A8"),
                    new ColorMap(null, "pastel", "F9F189"),
                    new ColorMap(null, "pastel", "F8EE74"),
                    new ColorMap(null, "pastel", "F1D95B"),
                    new ColorMap(null, "pastel", "EBC251"),
                    new ColorMap(null, "pastel", "D8EFC5"),
                    new ColorMap(null, "pastel", "BFE3AA"),
                    new ColorMap(null, "pastel", "A8D98F"),
                    new ColorMap(null, "pastel", "92CD76"),
                    new ColorMap(null, "pastel", "7EC35E"),
                    new ColorMap(null, "pastel", "64A743"),
                    new ColorMap(null, "pastel", "DEEFFD"),
                    new ColorMap(null, "pastel", "C3E4FC"),
                    new ColorMap(null, "pastel", "A9D7FB"),
                    new ColorMap(null, "pastel", "91CAFA"),
                    new ColorMap(null, "pastel", "7CBDF9"),
                    new ColorMap(null, "pastel", "5C97C7"),
                    new ColorMap(null, "pastel", "E7E6F8"),
                    new ColorMap(null, "pastel", "D2C0D6"),
                    new ColorMap(null, "pastel", "C5A6DA"),
                    new ColorMap(null, "pastel", "AF84CC"),
                    new ColorMap(null, "pastel", "905DB0"),
                    new ColorMap(null, "pastel", "744493"),
                    new ColorMap(null, "pastel", "F5F5F5"),
                    new ColorMap(null, "pastel", "E0E0E0"),
                    new ColorMap(null, "pastel", "CCCCCC"),
                    new ColorMap(null, "pastel", "B3B3B3"),
                    new ColorMap(null, "pastel", "999999"),
                    new ColorMap(null, "pastel", "7F7F7F"),
                    new ColorMap(null, "pastel", "000000"),
                    new ColorMap(null, "pastel", "FFFFFF"),
                    new ColorMap(null, "pastel", "DDDDDD"),
                    new ColorMap(null, "pastel", "444444"),
                    new ColorMap(null, "pastel", "FAF9F6"),
                    new ColorMap(null, "pastel", "C0C0C0"),
//
//                    // vivid
                    new ColorMap(null, "vivid", "DE3323"),
                    new ColorMap(null, "vivid", "B1271A"),
                    new ColorMap(null, "vivid", "851A11"),
                    new ColorMap(null, "vivid", "580E08"),
                    new ColorMap(null, "vivid", "E26F2E"),
                    new ColorMap(null, "vivid", "B55923"),
                    new ColorMap(null, "vivid", "874218"),
                    new ColorMap(null, "vivid", "5A2C0D"),
                    new ColorMap(null, "vivid", "FCFE57"),
                    new ColorMap(null, "vivid", "C9CB44"),
                    new ColorMap(null, "vivid", "979831"),
                    new ColorMap(null, "vivid", "65661E"),
                    new ColorMap(null, "vivid", "6AC83E"),
                    new ColorMap(null, "vivid", "4E962C"),
                    new ColorMap(null, "vivid", "32641A"),
                    new ColorMap(null, "vivid", "163209"),
                    new ColorMap(null, "vivid", "4767F5"),
                    new ColorMap(null, "vivid", "2E4C93"),
                    new ColorMap(null, "vivid", "1D3362"),
                    new ColorMap(null, "vivid", "0B1931"),
                    new ColorMap(null, "vivid", "8C25F5"),
                    new ColorMap(null, "vivid", "6A1BC3"),
                    new ColorMap(null, "vivid", "481092"),
                    new ColorMap(null, "vivid", "2F0761"),
                    new ColorMap(null, "vivid", "E04997"),
                    new ColorMap(null, "vivid", "B33A78"),
                    new ColorMap(null, "vivid", "862B5A"),
                    new ColorMap(null, "vivid", "591D3C"),
                    new ColorMap(null, "vivid", "905636"),
                    new ColorMap(null, "vivid", "6D3F27"),
                    new ColorMap(null, "vivid", "4C2A19"),
                    new ColorMap(null, "vivid", "26150C"),
                    new ColorMap(null, "vivid", "6197C7"),
                    new ColorMap(null, "vivid", "487295"),
                    new ColorMap(null, "vivid", "304C64"),
                    new ColorMap(null, "vivid", "172632"),
                    new ColorMap(null, "vivid", "979846"),
                    new ColorMap(null, "vivid", "727334"),
                    new ColorMap(null, "vivid", "4C4D23"),
                    new ColorMap(null, "vivid", "252611"),
                    new ColorMap(null, "vivid", "78C9CB"),
                    new ColorMap(null, "vivid", "599798"),
                    new ColorMap(null, "vivid", "3B6565"),
                    new ColorMap(null, "vivid", "1C3233"),
                    new ColorMap(null, "vivid", "242424"),
                    new ColorMap(null, "vivid", "F7F4F4"),
                    new ColorMap(null, "vivid", "999999"),
                    new ColorMap(null, "vivid", "333333"),
//
//                    // nature
                    new ColorMap(null, "nature", "DFF6FD"),
                    new ColorMap(null, "nature", "CBF2FD"),
                    new ColorMap(null, "nature", "ABE9FE"),
                    new ColorMap(null, "nature", "8EDBFE"),
                    new ColorMap(null, "nature", "5ABAF4"),
                    new ColorMap(null, "nature", "1985C3"),
                    new ColorMap(null, "nature", "DFF6FC"),
                    new ColorMap(null, "nature", "D2F2FC"),
                    new ColorMap(null, "nature", "A2E8FB"),
                    new ColorMap(null, "nature", "68D3F8"),
                    new ColorMap(null, "nature", "40B7E7"),
                    new ColorMap(null, "nature", "0D8ABB"),
                    new ColorMap(null, "nature", "D7F1F2"),
                    new ColorMap(null, "nature", "BAEBE6"),
                    new ColorMap(null, "nature", "9EE2DB"),
                    new ColorMap(null, "nature", "5BCEC6"),
                    new ColorMap(null, "nature", "3BB9BF"),
                    new ColorMap(null, "nature", "10809C"),
                    new ColorMap(null, "nature", "D2EEEC"),
                    new ColorMap(null, "nature", "C1E6E0"),
                    new ColorMap(null, "nature", "8FDBD2"),
                    new ColorMap(null, "nature", "6AC6BB"),
                    new ColorMap(null, "nature", "3FB9A5"),
                    new ColorMap(null, "nature", "11788A"),
                    new ColorMap(null, "nature", "DBE8D6"),
                    new ColorMap(null, "nature", "C6D9B7"),
                    new ColorMap(null, "nature", "99BB7F"),
                    new ColorMap(null, "nature", "699860"),
                    new ColorMap(null, "nature", "4C7B4D"),
                    new ColorMap(null, "nature", "3D6547"),
                    new ColorMap(null, "nature", "EAE5D7"),
                    new ColorMap(null, "nature", "E5D6BD"),
                    new ColorMap(null, "nature", "D3C09C"),
                    new ColorMap(null, "nature", "B4A17D"),
                    new ColorMap(null, "nature", "978161"),
                    new ColorMap(null, "nature", "5F4E3E"),
                    new ColorMap(null, "nature", "F2DFB6"),
                    new ColorMap(null, "nature", "F3C386"),
                    new ColorMap(null, "nature", "F0AA58"),
                    new ColorMap(null, "nature", "D66D44"),
                    new ColorMap(null, "nature", "B95145"),
                    new ColorMap(null, "nature", "884642"),
                    new ColorMap(null, "nature", "F1CBB0"),
                    new ColorMap(null, "nature", "EFB481"),
                    new ColorMap(null, "nature", "E38373"),
                    new ColorMap(null, "nature", "CE656C"),
                    new ColorMap(null, "nature", "96547D"),
                    new ColorMap(null, "nature", "7D466A")
            );

            colorMapRepository.saveAll(colors);
        }

        /*
        List<Member> members = memberRepository.findAll();
        List<ColorMap> allColors = colorMapRepository.findAll();

        //회원가입 시 usercolormap이 자동으로 생성될 수 있도록 코드 위치 수정 필요합니다!!
        for (Member member : members) {
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
         */

    }
}
