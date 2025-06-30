package makeus.cmc.malmo.domain.model.member;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.LoveType;

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
    private LoveType loveType;
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

    public void refreshMemberToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public CoupleCode generateCoupleCode(String inviteCode, LocalDate startLoveDate) {
        return CoupleCode.builder()
                .inviteCode(inviteCode)
                .startLoveDate(startLoveDate)
                .memberId(this.id)
                .build();
    }
}