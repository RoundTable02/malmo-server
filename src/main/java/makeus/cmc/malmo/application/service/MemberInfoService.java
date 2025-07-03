package makeus.cmc.malmo.application.service;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.exception.MemberNotFoundException;
import makeus.cmc.malmo.application.port.in.GetMemberUseCase;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInfoService implements GetMemberUseCase {

    private final LoadMemberPort loadMemberPort;

    @Override
    public MemberResponseDto getMemberInfo(MemberInfoCommand command) {
        Member member = loadMemberPort.loadMemberById(command.getUserId())
                .orElseThrow(MemberNotFoundException::new);

        return MemberResponseDto.builder()
                .memberState(member.getMemberState())
                .loveTypeTitle(member.getLoveType().getTitle())
                .avoidanceRate(member.getAvoidanceRate())
                .anxietyRate(member.getAnxietyRate())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
