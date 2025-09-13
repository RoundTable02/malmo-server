package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.notification.MemberNotificationEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.MemberNotificationMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.notification.MemberNotificationRepository;
import makeus.cmc.malmo.application.port.out.notification.LoadNotificationPort;
import makeus.cmc.malmo.application.port.out.notification.SaveNotificationPort;
import makeus.cmc.malmo.domain.model.notification.MemberNotification;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.NotificationState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberNotificationPersistenceAdapter implements LoadNotificationPort, SaveNotificationPort {

    private final MemberNotificationRepository memberNotificationRepository;
    private final MemberNotificationMapper memberNotificationMapper;

    @Override
    public List<MemberNotification> getNotificationsByMemberIdAndState(MemberId memberId, NotificationState state) {
        return memberNotificationRepository.findByMemberIdAndState(memberId.getValue(), state)
                .stream()
                .map(memberNotificationMapper::toDomain)
                .toList();
    }

    @Override
    public boolean validateAllNotificationsOwnership(MemberId memberId, List<Long> notificationIds) {
        return memberNotificationRepository.areAllNotificationsBelongToMember(notificationIds, memberId.getValue(), notificationIds.size());
    }

    @Override
    public MemberNotification saveNotification(MemberNotification memberNotification) {
        MemberNotificationEntity entity = memberNotificationMapper.toEntity(memberNotification);
        MemberNotificationEntity savedEntity = memberNotificationRepository.save(entity);
        return memberNotificationMapper.toDomain(savedEntity);
    }

    @Override
    public void readNotifications(List<Long> notificationIds) {
        memberNotificationRepository.readNotifications(notificationIds);
    }
}
