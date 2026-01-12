CREATE TABLE bookmark_entity
(
    bookmark_id     BIGINT       NOT NULL,
    created_at      datetime     NULL,
    modified_at     datetime     NULL,
    deleted_at      datetime     NULL,
    bookmark_state  VARCHAR(255) NULL,
    chat_room_id    BIGINT       NULL,
    chat_message_id BIGINT       NULL,
    member_id       BIGINT       NOT NULL,
    CONSTRAINT pk_bookmark_entity PRIMARY KEY (bookmark_id)
);

CREATE INDEX idx_bookmark_member_chatroom ON bookmark_entity (member_id, chat_room_id);

CREATE INDEX idx_bookmark_member_message ON bookmark_entity (member_id, chat_message_id);