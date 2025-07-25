package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.ChatRoomRepository;
import makeus.cmc.malmo.adaptor.out.persistence.repository.CoupleQuestionRepository;
import makeus.cmc.malmo.adaptor.out.persistence.repository.MemberRepository;
import makeus.cmc.malmo.application.port.out.*;
import makeus.cmc.malmo.application.service.MemberInfoService;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements
        LoadMemberPort, SaveMemberPort, LoadPartnerPort, LoadInviteCodePort, LoadChatRoomMetadataPort {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    private final ChatRoomRepository chatRoomRepository;
    private final CoupleQuestionRepository coupleQuestionRepository;

    @Override
    public Optional<Member> loadMemberByProviderId(Provider provider, String providerId) {
        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .map(memberMapper::toDomain);
    }

    @Override
    public Optional<Member> loadMemberById(MemberId memberId) {
        return memberRepository.findById(memberId.getValue())
                .map(memberMapper::toDomain);
    }

    @Override
    public Optional<MemberInfoService.MemberInfoDto> loadMemberDetailsById(MemberId memberId) {
        int questionCount = coupleQuestionRepository.countCoupleQuestionsByMemberId(memberId.getValue());
        int chatRoomCount = chatRoomRepository.countChatRoomsByMemberId(memberId.getValue());
        return memberRepository.findMemberDetailsById(memberId.getValue())
                .map(dto -> dto.toDto(chatRoomCount, questionCount));
    }

    @Override
    public Optional<Member> loadMemberByInviteCode(InviteCodeValue inviteCode) {
        return memberRepository.findMemberEntityByInviteCode(inviteCode.getValue())
                .map(memberMapper::toDomain);
    }

    @Override
    public Member saveMember(Member member) {
        MemberEntity memberEntity = memberMapper.toEntity(member);
        MemberEntity savedEntity = memberRepository.save(memberEntity);
        return memberMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PartnerMemberRepositoryDto> loadPartnerByMemberId(Long memberId) {
        return memberRepository.findPartnerMember(memberId);
    }

    @Override
    public Optional<InviteCodeValue> loadInviteCodeByMemberId(MemberId memberId) {
        return memberRepository.findInviteCodeByMemberId(memberId.getValue())
                .map(code -> InviteCodeValue.of(code.getValue()));
    }

    @Override
    public Optional<ChatRoomMetadataDto> loadChatRoomMetadata(MemberId memberId) {
        return memberRepository.loadChatRoomMetadata(memberId.getValue());
    }

    @Data
    @AllArgsConstructor
    public static class MemberResponseRepositoryDto {
        private String memberState;
        private LocalDate startLoveDate;
        private LoveTypeCategory loveTypeCategory;
        private float avoidanceRate;
        private float anxietyRate;
        private String nickname;
        private String email;

        public MemberInfoService.MemberInfoDto toDto(int totalChatRoomCount, int totalCoupleQuestionCount) {
            return MemberInfoService.MemberInfoDto.builder()
                    .memberState(memberState)
                    .startLoveDate(startLoveDate)
                    .loveTypeCategory(loveTypeCategory)
                    .avoidanceRate(avoidanceRate)
                    .anxietyRate(anxietyRate)
                    .nickname(nickname)
                    .email(email)
                    .totalChatRoomCount(totalChatRoomCount)
                    .totalCoupleQuestionCount(totalCoupleQuestionCount)
                    .build();
        }
    }
}
