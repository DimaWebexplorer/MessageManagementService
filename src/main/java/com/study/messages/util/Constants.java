package com.study.messages.util;

public final class Constants {

    private Constants() {
    }

    public static final String CREATE_MESSAGE = """
            INSERT INTO messages_db.messages (text, author_id)
            VALUES (?, ?)
            RETURNING id, text, author_id, created_at, updated_at
            """;

    public static final String GET_MESSAGE_BY_ID = """
            SELECT id, text, author_id, created_at, updated_at
            FROM messages_db.messages
            WHERE id = ?
            """;

    public static final String GET_MESSAGES_BY_AUTHOR = """
            SELECT id, text, author_id, created_at, updated_at
            FROM messages_db.messages
            WHERE author_id = ?
            ORDER BY created_at DESC, id DESC
            LIMIT ?
            """;

    public static final String GET_MESSAGES_BY_AUTHOR_BEFORE = """
            SELECT id, text, author_id, created_at, updated_at
            FROM messages_db.messages
            WHERE author_id = ?
              AND (created_at, id) < (?, ?)
            ORDER BY created_at DESC, id DESC
            LIMIT ?
            """;

    public static final String UPDATE_MESSAGE = """
            UPDATE messages_db.messages
            SET text = ?,
                updated_at = now()
            WHERE id = ?
            RETURNING id, text, author_id, created_at, updated_at
            """;

    public static final String DELETE_MESSAGE = """
            DELETE FROM messages_db.messages
            WHERE id = ?
            """;

    public static final String GET_FEED = """
            SELECT m.id, m.text, m.author_id, m.created_at, m.updated_at
            FROM messages_db.subscriptions s
            JOIN messages_db.messages m ON m.author_id = s.producer_id
            WHERE s.subscriber_id = ?
            ORDER BY m.created_at DESC, m.id DESC
            LIMIT ?
            """;

    public static final String GET_FEED_BEFORE = """
            SELECT m.id, m.text, m.author_id, m.created_at, m.updated_at
            FROM messages_db.subscriptions s
            JOIN messages_db.messages m ON m.author_id = s.producer_id
            WHERE s.subscriber_id = ?
              AND (m.created_at, m.id) < (?, ?)
            ORDER BY m.created_at DESC, m.id DESC
            LIMIT ?
            """;

    public static final String CREATE_SUBSCRIPTION = """
            INSERT INTO messages_db.subscriptions (subscriber_id, producer_id)
            VALUES (?, ?)
            RETURNING subscriber_id, producer_id, created_at
            """;

    public static final String DELETE_SUBSCRIPTION = """
            DELETE FROM messages_db.subscriptions
            WHERE subscriber_id = ? AND producer_id = ?
            """;

    public static final String GET_MY_SUBSCRIPTIONS = """
            SELECT producer_id, created_at AS subscribed_at
            FROM messages_db.subscriptions
            WHERE subscriber_id = ?
            ORDER BY created_at DESC, producer_id ASC
            """;

    public static final String CHECK_SUBSCRIPTION = """
            SELECT EXISTS (
                SELECT 1
                FROM messages_db.subscriptions
                WHERE subscriber_id = ? AND producer_id = ?
            )
            """;

    public static final String GET_FOLLOWERS = """
            SELECT subscriber_id
            FROM messages_db.subscriptions
            WHERE producer_id = ?
              AND subscriber_id > ?
            ORDER BY subscriber_id
            LIMIT ?
            """;

    public static final String GET_STATS = """
            SELECT (SELECT count(*) FROM messages_db.subscriptions WHERE producer_id = ?)   AS followers_count,
                   (SELECT count(*) FROM messages_db.subscriptions WHERE subscriber_id = ?) AS following_count
            """;
}
