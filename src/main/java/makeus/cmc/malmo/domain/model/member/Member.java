package makeus.cmc.malmo.domain.model.member;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;
import makeus.cmc.malmo.domain.model.LoveType;

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
    
    public static Member createMember(Provider provider, String providerId, MemberRole memberRole, MemberState memberState, LoveType loveType) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .memberRole(memberRole)
                .memberState(memberState)
                .loveType(loveType)
                .isAlarmOn(true)
                .build();
    }

    public void refreshMemberToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}