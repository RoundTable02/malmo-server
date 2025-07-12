package makeus.cmc.malmo.domain.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.id.LoveTypeId;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;

import java.time.LocalDate;

@Getter
@SuperBuilder
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    private Long id;
    private Provider provider;
    private String providerId;
    private MemberRole memberRole;
    private MemberState memberState;
    private boolean isAlarmOn;
    private String firebaseToken;
    private String refreshToken;
    private LoveTypeId loveTypeId;
    private float avoidanceRate;
    private float anxietyRate;
    private String nickname;
    private String email;
    private InviteCodeValue inviteCode;
    private LocalDate startLoveDate;
    
    public static Member createMember(Provider provider, String providerId, MemberRole memberRole, MemberState memberState, String email) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .memberRole(memberRole)
                .memberState(memberState)
                .email(email)
                .build();
    }

    public void signUp(String nickname, LocalDate startLoveDate) {
        this.nickname = nickname;
        this.startLoveDate = startLoveDate;
        this.memberState = MemberState.ALIVE;
    }

    public void updateInviteCode(InviteCodeValue inviteCode) {
        this.inviteCode = inviteCode;
    }

    public void updateMemberProfile(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public void updateLoveTypeId(LoveTypeId loveTypeId, float avoidanceRate, float anxietyRate) {
        this.loveTypeId = loveTypeId;
        this.avoidanceRate = avoidanceRate / 18.0f;
        this.anxietyRate = anxietyRate / 18.0f;
    }

    public void refreshMemberToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}