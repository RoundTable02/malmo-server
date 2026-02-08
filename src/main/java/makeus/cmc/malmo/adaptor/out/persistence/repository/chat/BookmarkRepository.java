package makeus.cmc.malmo.adaptor.out.persistence.repository.chat;

import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.BookmarkEntity;
import makeus.cmc.malmo.domain.value.state.BookmarkState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long>, BookmarkRepositoryCustom {

    Optional<BookmarkEntity> findByIdAndBookmarkState(Long id, BookmarkState state);

    Optional<BookmarkEntity> findByMemberEntityIdValueAndChatMessageEntityIdValueAndBookmarkState(
            Long memberId, Long chatMessageId, BookmarkState state);

    boolean existsByMemberEntityIdValueAndChatMessageEntityIdValueAndBookmarkState(
            Long memberId, Long chatMessageId, BookmarkState state);
}
