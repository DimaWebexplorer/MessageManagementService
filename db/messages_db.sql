CREATE SCHEMA IF NOT EXISTS messages_db;

CREATE TABLE messages_db.messages (
    id         SERIAL PRIMARY KEY,
    text       TEXT NOT NULL,
    author_id  INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX ON messages_db.messages (author_id, created_at DESC, id DESC);

CREATE TABLE messages_db.subscriptions (
    subscriber_id INTEGER NOT NULL,
    producer_id   INTEGER NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT now(),
    PRIMARY KEY (subscriber_id, producer_id),
    CHECK (subscriber_id <> producer_id)
);

CREATE INDEX ON messages_db.subscriptions (producer_id, subscriber_id);

INSERT INTO messages_db.messages (id, text, author_id, created_at) VALUES
(1, 'Завтра в 19:00 лекция', 1, '2026-09-27 09:00+05'),
(2, 'Новая статья про индексы', 4, '2026-09-27 11:00+05'),
(3, 'Перенесли на 20:00', 1, '2026-09-27 13:00+05'),
(4, 'Релиз в пятницу', 5, '2026-09-27 14:00+05'),
(5, 'Разбор ДЗ выложен', 4, '2026-09-27 14:30+05'),
(6, 'Кто идёт?', 2, '2026-09-27 15:00+05');

SELECT setval('messages_db.messages_id_seq', 6);

INSERT INTO messages_db.subscriptions (subscriber_id, producer_id) VALUES
(2, 1), (2, 4),
(6, 1), (6, 4),
(7, 4), (7, 5);
