package makeus.cmc.malmo.domain.model.member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Member {
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
    private String oauthToken;
    private CoupleId coupleId;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;


    public static Member createMember(Provider provider, String providerId, MemberRole memberRole, MemberState memberState, String email, InviteCodeValue inviteCode, String oauthToken) {
        return Member.builder()
                .provider(provider)
                .providerId(providerId)
                .memberRole(memberRole)
                .memberState(memberState)
                .email(email)
                .inviteCode(inviteCode)
                .oauthToken(oauthToken)
                .build();
    }

    public static Member from(
            Long id,
            Provider provider,
            String providerId,
            MemberRole memberRole,
            MemberState memberState,
            boolean isAlarmOn,
            String firebaseToken,
            String refreshToken,
            LoveTypeCategory loveTypeCategory,
            float avoidanceRate,
            float anxietyRate,
            String nickname,
            String email,
            InviteCodeValue inviteCode,
            LocalDate startLoveDate,
            String oauthToken,
            CoupleId coupleId,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            LocalDateTime deletedAt
    ) {
        return Member.builder()
                .id(id)
                .provider(provider)
                .providerId(providerId)
                .memberRole(memberRole)
                .memberState(memberState)
                .isAlarmOn(isAlarmOn)
                .firebaseToken(firebaseToken)
                .refreshToken(refreshToken)
                .loveTypeCategory(loveTypeCategory)
                .avoidanceRate(avoidanceRate)
                .anxietyRate(anxietyRate)
                .nickname(nickname)
                .email(email)
                .inviteCode(inviteCode)
                .startLoveDate(startLoveDate)
                .oauthToken(oauthToken)
                .coupleId(coupleId)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }


    public void signUp(String nickname, LocalDate startLoveDate) {
        this.nickname = nickname;
        this.startLoveDate = startLoveDate;
        this.memberState = MemberState.ALIVE;
    }

    public void updateMemberProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updateLoveType(LoveTypeCategory loveTypeCategory, float avoidanceRate, float anxietyRate) {
        this.loveTypeCategory = loveTypeCategory;
        this.avoidanceRate = avoidanceRate;
        this.anxietyRate = anxietyRate;
    }

    public void refreshMemberToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateStartLoveDate(LocalDate startLoveDate) {
        this.startLoveDate = startLoveDate;
    }

    public void delete() {
        this.memberState = MemberState.DELETED;
        this.deletedAt = LocalDateTime.now();
        this.providerId = this.providerId + "_deleted";
    }

    public boolean hasSameRefreshToken(String refreshToken) {
        return this.refreshToken != null && this.refreshToken.equals(refreshToken);
    }

    public void logOut() {
        this.refreshToken = null;
        this.firebaseToken = null;
    }

    public boolean isCoupleLinked() {
        return this.coupleId != null;
    }

    public void linkCouple(CoupleId coupleId) {
        this.coupleId = coupleId;
    }

    public void unlinkCouple() {
        this.coupleId = null;
    }
}