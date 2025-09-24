package makeus.cmc.malmo.adaptor.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import makeus.cmc.malmo.domain.value.state.OutboxState;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OutboxEntity extends BaseTimeEntity {

    @Column(name = "outboxId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = AESGCMConverter.class)
    private String payload;

    private int retryCount;

    @Enumerated(EnumType.STRING)
    private OutboxState state;

    private String messageId;
}
