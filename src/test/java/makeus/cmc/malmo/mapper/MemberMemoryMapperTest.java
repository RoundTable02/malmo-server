package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberMemoryEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.CoupleEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.MemberEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberMemoryMapper;
import makeus.cmc.malmo.domain.model.member.MemberMemory;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.MemberMemoryState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberMemoryMapper 테스트")
class MemberMemoryMapperTest {

    @InjectMocks
    private MemberMemoryMapper memberMemoryMapper;

    @Test
    @DisplayName("Entity를 Domain으로 변환한다")
    void toDomain() {
        // given
        LocalDateTime now = LocalDateTime.now();
        MemberMemoryEntity entity = MemberMemoryEntity.builder()
                .id(1L)
                .coupleEntityId(CoupleEntityId.of(100L))
                .memberEntityId(MemberEntityId.of(200L))
                .content("memory content")
                .memberMemoryState(MemberMemoryState.ALIVE)
                .createdAt(now)
                .modifiedAt(now)
                .deletedAt(null)
                .build();

        // when
        MemberMemory domain = memberMemoryMapper.toDomain(entity);

        // then
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getCoupleId().getValue()).isEqualTo(entity.getCoupleEntityId().getValue());
        assertThat(domain.getMemberId().getValue()).isEqualTo(entity.getMemberEntityId().getValue());
        assertThat(domain.getContent()).isEqualTo(entity.getContent());
        assertThat(domain.getMemberMemoryState()).isEqualTo(entity.getMemberMemoryState());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getModifiedAt()).isEqualTo(entity.getModifiedAt());
        assertThat(domain.getDeletedAt()).isEqualTo(entity.getDeletedAt());
    }

    @Test
    @DisplayName("Domain을 Entity로 변환한다")
    void toEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        MemberMemory domain = MemberMemory.from(
                1L,
                CoupleId.of(100L),
                MemberId.of(200L),
                "memory content",
                MemberMemoryState.ALIVE,
                now,
                now,
                null
        );

        // when
        MemberMemoryEntity entity = memberMemoryMapper.toEntity(domain);

        // then
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getCoupleEntityId().getValue()).isEqualTo(domain.getCoupleId().getValue());
        assertThat(entity.getMemberEntityId().getValue()).isEqualTo(domain.getMemberId().getValue());
        assertThat(entity.getContent()).isEqualTo(domain.getContent());
        assertThat(entity.getMemberMemoryState()).isEqualTo(domain.getMemberMemoryState());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getModifiedAt()).isEqualTo(domain.getModifiedAt());
        assertThat(entity.getDeletedAt()).isEqualTo(domain.getDeletedAt());
    }

    @Test
    @DisplayName("null Entity를 Domain으로 변환하면 null을 반환한다")
    void toDomain_withNullEntity() {
        // when
        MemberMemory domain = memberMemoryMapper.toDomain(null);

        // then
        assertThat(domain).isNull();
    }

    @Test
    @DisplayName("null Domain을 Entity로 변환하면 null을 반환한다")
    void toEntity_withNullDomain() {
        // when
        MemberMemoryEntity entity = memberMemoryMapper.toEntity(null);

        // then
        assertThat(entity).isNull();
    }
}