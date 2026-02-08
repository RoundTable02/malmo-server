package makeus.cmc.malmo.domain.value.id;

import lombok.Value;

@Value
public class BookmarkId {
    Long value;

    public static BookmarkId of(Long value) {
        return new BookmarkId(value);
    }
}
