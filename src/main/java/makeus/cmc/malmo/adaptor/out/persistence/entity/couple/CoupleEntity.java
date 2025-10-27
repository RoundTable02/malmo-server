package makeus.cmc.malmo.adaptor.out.persistence.entity.couple;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.adaptor.out.persistence.entity.BaseTimeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.domain.value.state.CoupleState;

import java.time.LocalDate;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CoupleEntity extends BaseTimeEntity {

    @Column(name = "coupleId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startLoveDate;

    @Enumerated(EnumType.STRING)
    private CoupleState coupleState;

    @Column(name = "is_start_love_date_updated", nullable = false)
    private Boolean isStartLoveDateUpdated = false;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "first_member_id"))
    })
    private MemberEntityId firstMemberId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "second_member_id"))
    })
    private MemberEntityId secondMemberId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "nickname", column = @Column(name = "first_member_nickname")),
            @AttributeOverride(name = "loveTypeCategory", column = @Column(name = "first_member_love_type_category")),
            @AttributeOverride(name = "avoidanceRate", column = @Column(name = "first_member_avoidance_rate")),
            @AttributeOverride(name = "anxietyRate", column = @Column(name = "first_member_anxiety_rate"))
    })
    private CoupleMemberSnapshotEntity firstMemberSnapshot;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "nickname", column = @Column(name = "second_member_nickname")),
            @AttributeOverride(name = "loveTypeCategory", column = @Column(name = "second_member_love_type_category")),
            @AttributeOverride(name = "avoidanceRate", column = @Column(name = "second_member_avoidance_rate")),
            @AttributeOverride(name = "anxietyRate", column = @Column(name = "second_member_anxiety_rate"))
    })
    private CoupleMemberSnapshotEntity secondMemberSnapshot;
}
