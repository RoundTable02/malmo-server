package makeus.cmc.malmo.domain.model.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makeus.cmc.malmo.domain.model.BaseTimeEntity;

@Getter
@NoArgsConstructor
@Entity
public class MemberMemory extends BaseTimeEntity {

    @Column(name = "memberMemoryId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String content;
}
