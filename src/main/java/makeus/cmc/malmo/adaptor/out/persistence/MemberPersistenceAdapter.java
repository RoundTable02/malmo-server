package makeus.cmc.malmo.adaptor.out.persistence;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.ProviderJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.InviteCodeEntityValue;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.MemberRepository;
import makeus.cmc.malmo.application.port.out.LoadInviteCodePort;
import makeus.cmc.malmo.application.port.out.LoadMemberPort;
import makeus.cmc.malmo.application.port.out.LoadPartnerPort;
import makeus.cmc.malmo.application.port.out.SaveMemberPort;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.Provider;
import makeus.cmc.malmo.domain.model.value.InviteCodeValue;
import makeus.cmc.malmo.domain.model.value.MemberId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements LoadMemberPort, SaveMemberPort, LoadPartnerPort, LoadInviteCodePort {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public Optional<Member> loadMemberByProviderId(Provider provider, String providerId) {
        ProviderJpa providerJpa = ProviderJpa.valueOf(provider.name());

        return memberRepository.findByProviderJpaAndProviderId(providerJpa, providerId)
                .map(memberMapper::toDomain);
    }

    @Override
    public Optional<Member> loadMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .map(memberMapper::toDomain);
    }

    @Override
    public Optional<MemberResponseRepositoryDto> loadMemberDetailsById(Long memberId) {
        return memberRepository.findMemberDetailsById(memberId);
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
}
