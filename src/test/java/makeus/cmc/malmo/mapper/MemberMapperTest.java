package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberMapper;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.value.id.CoupleId;
import makeus.cmc.malmo.domain.value.id.InviteCodeValue;
import makeus.cmc.malmo.domain.value.state.MemberState;
import makeus.cmc.malmo.domain.value.type.LoveTypeCategory;
import makeus.cmc.malmo.domain.value.type.MemberRole;
import makeus.cmc.malmo.domain.value.type.Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberMapper 테스트")
class MemberMapperTest {

    @InjectMocks
    private MemberMapper memberMapper;

    @Nested
    @DisplayName("Entity를 Domain으로 변환할 때")
    class ToDomainTest {

        @Test
        @DisplayName("모든 필드가 있는 MemberEntity를 Member로 변환한다")
        void givenCompleteEntity_whenToDomain_thenReturnsCompleteMember() {
            // given
            MemberEntity entity = createCompleteEntity();

            // when
            Member result = memberMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getProvider()).isEqualTo(Provider.KAKAO);
            assertThat(result.getProviderId()).isEqualTo("provider123");
            assertThat(result.getMemberRole()).isEqualTo(MemberRole.MEMBER);
            assertThat(result.getMemberState()).isEqualTo(MemberState.ALIVE);
            assertThat(result.isAlarmOn()).isTrue();
            assertThat(result.getFirebaseToken()).isEqualTo("firebase_token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh_token");
            assertThat(result.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.STABLE_TYPE);
            assertThat(result.getAvoidanceRate()).isEqualTo(0.5f);
            assertThat(result.getAnxietyRate()).isEqualTo(0.3f);
            assertThat(result.getNickname()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("null 값이 포함된 MemberEntity를 Member로 변환한다")
        void givenEntityWithNulls_whenToDomain_thenReturnsValidMember() {
            // given
            MemberEntity entity = createEntityWithNulls();

            // when
            Member result = memberMapper.toDomain(entity);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getProvider()).isNull();
            assertThat(result.getMemberRole()).isNull();
            assertThat(result.getMemberState()).isNull();
            assertThat(result.getLoveTypeCategory()).isNull();
        }

        @Test
        @DisplayName("Apple Provider를 가진 Entity를 변환한다")
        void givenAppleProvider_whenToDomain_thenReturnsAppleProvider() {
            // given
            MemberEntity entity = createEntityWithProvider(Provider.APPLE);

            // when
            Member result = memberMapper.toDomain(entity);

            // then
            assertThat(result.getProvider()).isEqualTo(Provider.APPLE);
        }

        @Test
        @DisplayName("ADMIN Role을 가진 Entity를 변환한다")
        void givenAdminRole_whenToDomain_thenReturnsAdminRole() {
            // given
            MemberEntity entity = createEntityWithRole(MemberRole.ADMIN);

            // when
            Member result = memberMapper.toDomain(entity);

            // then
            assertThat(result.getMemberRole()).isEqualTo(MemberRole.ADMIN);
        }

        @Test
        @DisplayName("다양한 MemberState를 가진 Entity를 변환한다")
        void givenDifferentStates_whenToDomain_thenReturnsCorrectStates() {
            // given
            // when & then
            MemberEntity beforeOnboardingEntity = createEntityWithState(MemberState.BEFORE_ONBOARDING);
            Member beforeOnboardingResult = memberMapper.toDomain(beforeOnboardingEntity);
            assertThat(beforeOnboardingResult.getMemberState()).isEqualTo(MemberState.BEFORE_ONBOARDING);

            MemberEntity deletedEntity = createEntityWithState(MemberState.DELETED);
            Member deletedResult = memberMapper.toDomain(deletedEntity);
            assertThat(deletedResult.getMemberState()).isEqualTo(MemberState.DELETED);
        }
    }

    @Nested
    @DisplayName("Domain을 Entity로 변환할 때")
    class ToEntityTest {

        @Test
        @DisplayName("모든 필드가 있는 Member를 MemberEntity로 변환한다")
        void givenCompleteMember_whenToEntity_thenReturnsCompleteEntity() {
            // given
            Member domain = createCompleteMember();

            // when
            MemberEntity result = memberMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getProvider()).isEqualTo(Provider.KAKAO);
            assertThat(result.getProviderId()).isEqualTo("provider123");
            assertThat(result.getMemberRole()).isEqualTo(MemberRole.MEMBER);
            assertThat(result.getMemberState()).isEqualTo(MemberState.ALIVE);
            assertThat(result.isAlarmOn()).isTrue();
            assertThat(result.getFirebaseToken()).isEqualTo("firebase_token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh_token");
            assertThat(result.getLoveTypeCategory()).isEqualTo(LoveTypeCategory.STABLE_TYPE);
            assertThat(result.getAvoidanceRate()).isEqualTo(0.5f);
            assertThat(result.getAnxietyRate()).isEqualTo(0.3f);
            assertThat(result.getNickname()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("null 값이 포함된 Member를 MemberEntity로 변환한다")
        void givenMemberWithNulls_whenToEntity_thenReturnsValidEntity() {
            // given
            Member domain = createMemberWithNulls();

            // when
            MemberEntity result = memberMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNull();
            assertThat(result.getLoveTypeCategory()).isNull();
            assertThat(result.getProvider()).isNull();
            assertThat(result.getMemberRole()).isNull();
            assertThat(result.getMemberState()).isNull();
        }

        @Test
        @DisplayName("Apple Provider를 가진 Member를 변환한다")
        void givenAppleProvider_whenToEntity_thenReturnsAppleProvider() {
            // given
            Member domain = createMemberWithProvider(Provider.APPLE);

            // when
            MemberEntity result = memberMapper.toEntity(domain);

            // then
            assertThat(result.getProvider()).isEqualTo(Provider.APPLE);
        }

        @Test
        @DisplayName("ADMIN Role을 가진 Member를 변환한다")
        void givenAdminRole_whenToEntity_thenReturnsAdminRole() {
            // given
            Member domain = createMemberWithRole(MemberRole.ADMIN);

            // when
            MemberEntity result = memberMapper.toEntity(domain);

            // then
            assertThat(result.getMemberRole()).isEqualTo(MemberRole.ADMIN);
        }

        @Test
        @DisplayName("다양한 MemberState를 가진 Member를 변환한다")
        void givenDifferentStates_whenToEntity_thenReturnsCorrectStates() {
            // given
            // when & then
            Member beforeOnboardingMember = createMemberWithState(MemberState.BEFORE_ONBOARDING);
            MemberEntity beforeOnboardingResult = memberMapper.toEntity(beforeOnboardingMember);
            assertThat(beforeOnboardingResult.getMemberState()).isEqualTo(MemberState.BEFORE_ONBOARDING);

            Member deletedMember = createMemberWithState(MemberState.DELETED);
            MemberEntity deletedResult = memberMapper.toEntity(deletedMember);
            assertThat(deletedResult.getMemberState()).isEqualTo(MemberState.DELETED);
        }
    }

    // Test data creation methods
    private MemberEntity createCompleteEntity() {
        return MemberEntity.builder()
                .id(1L)
                .provider(Provider.KAKAO)
                .providerId("provider123")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .isAlarmOn(true)
                .firebaseToken("firebase_token")
                .refreshToken("refresh_token")
                .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                .avoidanceRate(0.5f)
                .anxietyRate(0.3f)
                .nickname("testuser")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    private MemberEntity createEntityWithNulls() {
        return MemberEntity.builder()
                .id(1L)
                .provider(null)
                .memberRole(null)
                .memberState(null)
                .loveTypeCategory(null)
                .build();
    }

    private MemberEntity createEntityWithProvider(Provider provider) {
        return MemberEntity.builder()
                .id(1L)
                .provider(provider)
                .build();
    }

    private MemberEntity createEntityWithRole(MemberRole role) {
        return MemberEntity.builder()
                .id(1L)
                .memberRole(role)
                .build();
    }

    private MemberEntity createEntityWithState(MemberState state) {
        return MemberEntity.builder()
                .id(1L)
                .memberState(state)
                .build();
    }

    private Member createCompleteMember() {
        return Member.from(
                1L,
                Provider.KAKAO,
                "provider123",
                MemberRole.MEMBER,
                MemberState.ALIVE,
                true,
                "firebase_token",
                "refresh_token",
                LoveTypeCategory.STABLE_TYPE,
                0.5f,
                0.3f,
                "testuser",
                "test@example.com",
                null, // InviteCodeValue
                LocalDate.now(),
                null, // oauthToken
                null, // CoupleId
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    private Member createMemberWithNulls() {
        return Member.createMember(
                null, null, null, null, null, null, null
        );
    }

    private Member createMemberWithProvider(Provider provider) {
        return Member.createMember(
                provider, "provider123", MemberRole.MEMBER, MemberState.ALIVE, "", null, null
        );
    }

    private Member createMemberWithRole(MemberRole role) {
        return Member.createMember(
                Provider.KAKAO, "provider123", role, MemberState.ALIVE, "", null, null
        );
    }

    private Member createMemberWithState(MemberState state) {
        return Member.createMember(
                Provider.KAKAO, "provider123", MemberRole.MEMBER, state, "", null, null
        );
    }
}
