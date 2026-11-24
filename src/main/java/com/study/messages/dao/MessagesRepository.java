package com.study.messages.dao;

import com.study.messages.dto.Message;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface MessagesRepository {

    Message createMessage(String text, int authorId);

    Optional<Message> findById(int id);

    List<Message> findByAuthor(int authorId, OffsetDateTime beforeTs, Integer beforeId, int limit);

    Optional<Message> updateMessage(int id, String text);

    boolean deleteMessage(int id);

    List<Message> findFeed(int subscriberId, OffsetDateTime beforeTs, Integer beforeId, int limit);
}
