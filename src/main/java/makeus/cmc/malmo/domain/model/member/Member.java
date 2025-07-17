package makeus.cmc.malmo.domain.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.value.InviteCodeValue;

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
    private LoveTypeCategory loveTypeCategory;
    private float avoidanceRate;
    private float anxietyRate;
    private String nickname;
    private String email;
    private InviteCodeValue inviteCode;
    private LocalDate startLoveDate;
    
    public static Member createMember(Provider provider, String providerId, MemberRole memberRole, MemberState memberState, String email, InviteCodeValue inviteCode) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .memberRole(memberRole)
                .memberState(memberState)
                .email(email)
                .inviteCode(inviteCode)
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

    public void updateLoveTypeId(LoveTypeCategory loveTypeCategory, float avoidanceRate, float anxietyRate) {
        this.loveTypeCategory = loveTypeCategory;
        this.avoidanceRate = avoidanceRate / 18.0f;
        this.anxietyRate = anxietyRate / 18.0f;
    }

    public void refreshMemberToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}