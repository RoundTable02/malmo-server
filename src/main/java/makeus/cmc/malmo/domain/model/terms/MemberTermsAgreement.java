package makeus.cmc.malmo.domain.model.terms;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.id.TermsId;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class MemberTermsAgreement {
    private Long id;
    private MemberId memberId;
    private TermsId termsId;
    private boolean agreed;

    // BaseTimeEntity fields
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static MemberTermsAgreement signTerms(MemberId memberId, TermsId termsId, boolean agreed) {
        return MemberTermsAgreement.builder()
                .memberId(memberId)
                .termsId(termsId)
                .agreed(agreed)
                .build();
    }

    public void updateAgreement(boolean agreed) {
        this.agreed = agreed;
    }

    public static MemberTermsAgreement from(Long id, MemberId memberId, TermsId termsId, boolean agreed,
                                            LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime deletedAt) {
        return MemberTermsAgreement.builder()
                .id(id)
                .memberId(memberId)
                .termsId(termsId)
                .agreed(agreed)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .deletedAt(deletedAt)
                .build();
    }
}