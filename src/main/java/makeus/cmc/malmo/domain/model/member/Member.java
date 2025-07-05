package makeus.cmc.malmo.domain.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.love_type.LoveType;
import makeus.cmc.malmo.domain.model.value.LoveTypeId;
import makeus.cmc.malmo.domain.model.value.MemberId;

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
    
    public static Member createMember(Provider provider, String providerId, MemberRole memberRole, MemberState memberState, String email) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .memberRole(memberRole)
                .memberState(memberState)
                .email(email)
                .build();
    }

    public void signUp(String nickname) {
        this.nickname = nickname;
        this.memberState = MemberState.ALIVE;
    }

    public void updateMemberProfile(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }

    public void updateLoveTypeId(LoveTypeId loveTypeId) {
        this.loveTypeId = loveTypeId;
    }

    public void refreshMemberToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public CoupleCode generateCoupleCode(String inviteCode, LocalDate startLoveDate) {
        return CoupleCode.builder()
                .inviteCode(inviteCode)
                .startLoveDate(startLoveDate)
                .memberId(MemberId.of(this.id))
                .build();
    }
}