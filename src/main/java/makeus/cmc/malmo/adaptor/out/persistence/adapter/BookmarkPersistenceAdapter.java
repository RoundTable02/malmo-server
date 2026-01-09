package makeus.cmc.malmo.adaptor.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import makeus.cmc.malmo.adaptor.out.persistence.entity.chat.BookmarkEntity;
import makeus.cmc.malmo.adaptor.out.persistence.mapper.BookmarkMapper;
import makeus.cmc.malmo.adaptor.out.persistence.repository.chat.BookmarkRepository;
import makeus.cmc.malmo.application.port.out.chat.DeleteBookmarkPort;
import makeus.cmc.malmo.application.port.out.chat.LoadBookmarkPort;
import makeus.cmc.malmo.application.port.out.chat.SaveBookmarkPort;
import makeus.cmc.malmo.domain.model.chat.Bookmark;
import makeus.cmc.malmo.domain.value.id.BookmarkId;
import makeus.cmc.malmo.domain.value.id.ChatRoomId;
import makeus.cmc.malmo.domain.value.id.MemberId;
import makeus.cmc.malmo.domain.value.state.BookmarkState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookmarkPersistenceAdapter implements LoadBookmarkPort, SaveBookmarkPort, DeleteBookmarkPort {

    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;

    @Override
    public Optional<Bookmark> loadBookmarkById(BookmarkId bookmarkId) {
        return bookmarkRepository.findByIdAndBookmarkState(bookmarkId.getValue(), BookmarkState.ALIVE)
                .map(bookmarkMapper::toDomain);
    }

    @Override
    public Optional<Bookmark> loadBookmarkByMemberAndMessage(MemberId memberId, Long chatMessageId) {
        return bookmarkRepository.findByMemberEntityIdValueAndChatMessageEntityIdValueAndBookmarkState(
                        memberId.getValue(), chatMessageId, BookmarkState.ALIVE)
                .map(bookmarkMapper::toDomain);
    }

    @Override
    public Page<BookmarkDto> loadBookmarksByMemberAndChatRoom(
            MemberId memberId, ChatRoomId chatRoomId, Pageable pageable) {
        return bookmarkRepository.loadBookmarksByMemberAndChatRoom(
                memberId.getValue(), chatRoomId.getValue(), pageable);
    }

    @Override
    public boolean existsByMemberAndMessage(MemberId memberId, Long chatMessageId) {
        return bookmarkRepository.existsByMemberEntityIdValueAndChatMessageEntityIdValueAndBookmarkState(
                memberId.getValue(), chatMessageId, BookmarkState.ALIVE);
    }

    @Override
    public boolean isMemberOwnerOfBookmarks(MemberId memberId, List<BookmarkId> bookmarkIds) {
        return bookmarkRepository.isMemberOwnerOfBookmarks(
                memberId.getValue(),
                bookmarkIds.stream().map(BookmarkId::getValue).toList());
    }

    @Override
    public long countMessagesBeforeId(ChatRoomId chatRoomId, Long messageId, String sort) {
        return bookmarkRepository.countMessagesBeforeId(chatRoomId.getValue(), messageId, sort);
    }

    @Override
    public Bookmark saveBookmark(Bookmark bookmark) {
        BookmarkEntity entity = bookmarkMapper.toEntity(bookmark);
        BookmarkEntity saved = bookmarkRepository.save(entity);
        return bookmarkMapper.toDomain(saved);
    }

    @Override
    public void softDeleteBookmarks(List<BookmarkId> bookmarkIds) {
        bookmarkRepository.softDeleteBookmarks(
                bookmarkIds.stream().map(BookmarkId::getValue).toList());
    }
}
