package makeus.cmc.malmo.domain;

import makeus.cmc.malmo.domain.model.couple.Couple;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.CoupleState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Couple 도메인 테스트")
class CoupleTest {

    @Nested
    @DisplayName("updateStartLoveDate 메서드")
    class UpdateStartLoveDateTest {

        @Test
        @DisplayName("디데이를 변경하면 isStartLoveDateUpdated가 true로 변경된다")
        void updateStartLoveDate_shouldSetIsStartLoveDateUpdatedToTrue() {
            // given
            Couple couple = createCoupleWithIsStartLoveDateUpdated(false);
            LocalDate newStartLoveDate = LocalDate.of(2024, 1, 1);

            // when
            couple.updateStartLoveDate(newStartLoveDate);

            // then
            assertThat(couple.getStartLoveDate()).isEqualTo(newStartLoveDate);
            assertThat(couple.getIsStartLoveDateUpdated()).isTrue();
        }

        @Test
        @DisplayName("이미 isStartLoveDateUpdated가 true인 경우에도 디데이 변경이 가능하다")
        void updateStartLoveDate_whenAlreadyTrue_shouldStillWork() {
            // given
            Couple couple = createCoupleWithIsStartLoveDateUpdated(true);
            LocalDate newStartLoveDate = LocalDate.of(2024, 1, 1);

            // when
            couple.updateStartLoveDate(newStartLoveDate);

            // then
            assertThat(couple.getStartLoveDate()).isEqualTo(newStartLoveDate);
            assertThat(couple.getIsStartLoveDateUpdated()).isTrue();
        }
    }

    @Nested
    @DisplayName("from 정적 팩토리 메서드")
    class FromMethodTest {

        @Test
        @DisplayName("isStartLoveDateUpdated 필드가 포함된 Couple을 생성한다")
        void from_shouldCreateCoupleWithIsStartLoveDateUpdated() {
            // given
            Long id = 1L;
            LocalDate startLoveDate = LocalDate.of(2023, 1, 1);
            MemberId firstMemberId = MemberId.of(100L);
            MemberId secondMemberId = MemberId.of(200L);
            CoupleState coupleState = CoupleState.ALIVE;
            LocalDateTime createdAt = LocalDateTime.now();
            LocalDateTime modifiedAt = LocalDateTime.now();
            LocalDateTime deletedAt = null;
            Boolean isStartLoveDateUpdated = false;

            // when
            Couple couple = Couple.from(id, startLoveDate, firstMemberId, secondMemberId, coupleState, null, null, createdAt, modifiedAt, deletedAt, isStartLoveDateUpdated);

            // then
            assertThat(couple.getId()).isEqualTo(id);
            assertThat(couple.getStartLoveDate()).isEqualTo(startLoveDate);
            assertThat(couple.getFirstMemberId()).isEqualTo(firstMemberId);
            assertThat(couple.getSecondMemberId()).isEqualTo(secondMemberId);
            assertThat(couple.getCoupleState()).isEqualTo(coupleState);
            assertThat(couple.getIsStartLoveDateUpdated()).isEqualTo(isStartLoveDateUpdated);
        }
    }

    private Couple createCoupleWithIsStartLoveDateUpdated(Boolean isStartLoveDateUpdated) {
        return Couple.from(
                1L,
                LocalDate.of(2023, 1, 1),
                MemberId.of(100L),
                MemberId.of(200L),
                CoupleState.ALIVE,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                isStartLoveDateUpdated
        );
    }
}
