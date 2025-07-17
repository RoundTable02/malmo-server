package makeus.cmc.malmo.mapper;

import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeCategoryJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.love_type.LoveTypeEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberEntity;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberRoleJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.MemberStateJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.member.ProviderJpa;
import makeus.cmc.malmo.adaptor.out.persistence.entity.value.LoveTypeEntityId;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberMapper;
import makeus.cmc.malmo.domain.model.love_type.LoveTypeCategory;
import makeus.cmc.malmo.domain.model.member.Member;
import makeus.cmc.malmo.domain.model.member.MemberRole;
import makeus.cmc.malmo.domain.model.member.MemberState;
import makeus.cmc.malmo.domain.model.member.Provider;
import makeus.cmc.malmo.domain.model.value.LoveTypeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
            LoveTypeEntity loveTypeEntity = createLoveTypeEntity();
            MemberEntity entity = createCompleteEntity(loveTypeEntity);

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
            assertThat(result.getLoveTypeId().getValue()).isEqualTo(100L);
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
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getProvider()).isNull();
            assertThat(result.getMemberRole()).isNull();
            assertThat(result.getMemberState()).isNull();
            assertThat(result.getLoveTypeId().getValue()).isNull();
        }

        @Test
        @DisplayName("Apple Provider를 가진 Entity를 변환한다")
        void givenAppleProvider_whenToDomain_thenReturnsAppleProvider() {
            // given
            MemberEntity entity = createEntityWithProvider(ProviderJpa.APPLE);

            // when
            Member result = memberMapper.toDomain(entity);

            // then
            assertThat(result.getProvider()).isEqualTo(Provider.APPLE);
        }

        @Test
        @DisplayName("ADMIN Role을 가진 Entity를 변환한다")
        void givenAdminRole_whenToDomain_thenReturnsAdminRole() {
            // given
            MemberEntity entity = createEntityWithRole(MemberRoleJpa.ADMIN);

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
            MemberEntity beforeOnboardingEntity = createEntityWithState(MemberStateJpa.BEFORE_ONBOARDING);
            Member beforeOnboardingResult = memberMapper.toDomain(beforeOnboardingEntity);
            assertThat(beforeOnboardingResult.getMemberState()).isEqualTo(MemberState.BEFORE_ONBOARDING);

            MemberEntity deletedEntity = createEntityWithState(MemberStateJpa.DELETED);
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
            LoveType loveTypeDomain = createLoveTypeDomain();
            Member domain = createCompleteMember(loveTypeDomain);

            // when
            MemberEntity result = memberMapper.toEntity(domain);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getProviderJpa()).isEqualTo(ProviderJpa.KAKAO);
            assertThat(result.getProviderId()).isEqualTo("provider123");
            assertThat(result.getMemberRoleJpa()).isEqualTo(MemberRoleJpa.MEMBER);
            assertThat(result.getMemberStateJpa()).isEqualTo(MemberStateJpa.ALIVE);
            assertThat(result.isAlarmOn()).isTrue();
            assertThat(result.getFirebaseToken()).isEqualTo("firebase_token");
            assertThat(result.getRefreshToken()).isEqualTo("refresh_token");
            assertThat(result.getLoveTypeEntityId().getValue()).isEqualTo(1L);
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
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getProviderJpa()).isNull();
            assertThat(result.getMemberRoleJpa()).isNull();
            assertThat(result.getMemberStateJpa()).isNull();
            assertThat(result.getLoveTypeEntityId()).isNull();
        }

        @Test
        @DisplayName("Apple Provider를 가진 Member를 변환한다")
        void givenAppleProvider_whenToEntity_thenReturnsAppleProvider() {
            // given
            Member domain = createMemberWithProvider(Provider.APPLE);

            // when
            MemberEntity result = memberMapper.toEntity(domain);

            // then
            assertThat(result.getProviderJpa()).isEqualTo(ProviderJpa.APPLE);
        }

        @Test
        @DisplayName("ADMIN Role을 가진 Member를 변환한다")
        void givenAdminRole_whenToEntity_thenReturnsAdminRole() {
            // given
            Member domain = createMemberWithRole(MemberRole.ADMIN);

            // when
            MemberEntity result = memberMapper.toEntity(domain);

            // then
            assertThat(result.getMemberRoleJpa()).isEqualTo(MemberRoleJpa.ADMIN);
        }

        @Test
        @DisplayName("다양한 MemberState를 가진 Member를 변환한다")
        void givenDifferentStates_whenToEntity_thenReturnsCorrectStates() {
            // given
            // when & then
            Member beforeOnboardingMember = createMemberWithState(MemberState.BEFORE_ONBOARDING);
            MemberEntity beforeOnboardingResult = memberMapper.toEntity(beforeOnboardingMember);
            assertThat(beforeOnboardingResult.getMemberStateJpa()).isEqualTo(MemberStateJpa.BEFORE_ONBOARDING);

            Member deletedMember = createMemberWithState(MemberState.DELETED);
            MemberEntity deletedResult = memberMapper.toEntity(deletedMember);
            assertThat(deletedResult.getMemberStateJpa()).isEqualTo(MemberStateJpa.DELETED);
        }
    }

    // Test data creation methods
    private MemberEntity createCompleteEntity(LoveTypeEntity loveTypeEntity) {
        return MemberEntity.builder()
                .id(1L)
                .providerJpa(ProviderJpa.KAKAO)
                .providerId("provider123")
                .memberRoleJpa(MemberRoleJpa.MEMBER)
                .memberStateJpa(MemberStateJpa.ALIVE)
                .isAlarmOn(true)
                .firebaseToken("firebase_token")
                .refreshToken("refresh_token")
                .loveTypeEntityId(LoveTypeEntityId.of(100L))
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
                .providerJpa(null)
                .memberRoleJpa(null)
                .memberStateJpa(null)
                .loveTypeEntityId(null)
                .build();
    }

    private MemberEntity createEntityWithProvider(ProviderJpa provider) {
        return MemberEntity.builder()
                .id(1L)
                .providerJpa(provider)
                .build();
    }

    private MemberEntity createEntityWithRole(MemberRoleJpa role) {
        return MemberEntity.builder()
                .id(1L)
                .memberRoleJpa(role)
                .build();
    }

    private MemberEntity createEntityWithState(MemberStateJpa state) {
        return MemberEntity.builder()
                .id(1L)
                .memberStateJpa(state)
                .build();
    }

    private Member createCompleteMember(LoveType loveType) {
        return Member.builder()
                .id(1L)
                .provider(Provider.KAKAO)
                .providerId("provider123")
                .memberRole(MemberRole.MEMBER)
                .memberState(MemberState.ALIVE)
                .isAlarmOn(true)
                .firebaseToken("firebase_token")
                .refreshToken("refresh_token")
                .loveTypeId(LoveTypeId.of(loveType.getId()))
                .avoidanceRate(0.5f)
                .anxietyRate(0.3f)
                .nickname("testuser")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    private Member createMemberWithNulls() {
        return Member.builder()
                .id(1L)
                .provider(null)
                .memberRole(null)
                .memberState(null)
                .loveTypeId(null)
                .build();
    }

    private Member createMemberWithProvider(Provider provider) {
        return Member.builder()
                .id(1L)
                .provider(provider)
                .build();
    }

    private Member createMemberWithRole(MemberRole role) {
        return Member.builder()
                .id(1L)
                .memberRole(role)
                .build();
    }

    private Member createMemberWithState(MemberState state) {
        return Member.builder()
                .id(1L)
                .memberState(state)
                .build();
    }

    private LoveTypeEntity createLoveTypeEntity() {
        return LoveTypeEntity.builder()
                .id(1L)
                .title("Test Love Type")
                .content("Test Content")
                .imageUrl("http://example.com/image.jpg")
                .loveTypeCategoryJpa(LoveTypeCategoryJpa.STABLE_TYPE)
                .build();
    }

    private LoveType createLoveTypeDomain() {
        return LoveType.builder()
                .id(1L)
                .title("Test Love Type")
                .content("Test Content")
                .imageUrl("http://example.com/image.jpg")
                .loveTypeCategory(LoveTypeCategory.STABLE_TYPE)
                .build();
    }
}