package makeus.cmc.malmo.adaptor.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TempLoveTypeEntity extends BaseTimeEntity {

    @Column(name = "tempLoveTypeId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoveTypeCategory category;

    private float avoidanceRate;

    private float anxietyRate;

}
