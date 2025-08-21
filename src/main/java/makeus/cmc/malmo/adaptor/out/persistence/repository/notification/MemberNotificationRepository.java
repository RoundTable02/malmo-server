package makeus.cmc.malmo.adaptor.out.persistence.repository.notification;

import makeus.cmc.malmo.adaptor.out.persistence.entity.notification.MemberNotificationEntity;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberNotificationRepository extends JpaRepository<MemberNotificationEntity, Long> {

    @Query("select m from MemberNotificationEntity m where m.memberId.value = ?1 and m.state = ?2")
    List<MemberNotificationEntity> findByMemberIdAndState(Long memberId, NotificationState state);
}
