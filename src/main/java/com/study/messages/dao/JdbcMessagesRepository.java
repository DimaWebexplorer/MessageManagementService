package com.study.messages.dao;

import com.study.messages.dto.Message;
import com.study.messages.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMessagesRepository implements MessagesRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DaoHelper daoHelper;

    @Override
    public Message createMessage(String text, int authorId) {
        return jdbcTemplate.queryForObject(Constants.CREATE_MESSAGE,
                (rs, rowNum) -> daoHelper.mapMessage(rs), text, authorId);
    }

    @Override
    public Optional<Message> findById(int id) {
        return jdbcTemplate.query(Constants.GET_MESSAGE_BY_ID,
                (rs, rowNum) -> daoHelper.mapMessage(rs), id).stream().findFirst();
    }

    @Override
    public List<Message> findByAuthor(int authorId, OffsetDateTime beforeTs, Integer beforeId, int limit) {
        if (beforeTs == null) {
            return jdbcTemplate.query(Constants.GET_MESSAGES_BY_AUTHOR,
                    (rs, rowNum) -> daoHelper.mapMessage(rs), authorId, limit);
        }
        return jdbcTemplate.query(Constants.GET_MESSAGES_BY_AUTHOR_BEFORE,
                (rs, rowNum) -> daoHelper.mapMessage(rs), authorId, beforeTs, beforeId, limit);
    }

    @Override
    public Optional<Message> updateMessage(int id, String text) {
        return jdbcTemplate.query(Constants.UPDATE_MESSAGE,
                (rs, rowNum) -> daoHelper.mapMessage(rs), text, id).stream().findFirst();
    }

    @Override
    public boolean deleteMessage(int id) {
        return jdbcTemplate.update(Constants.DELETE_MESSAGE, id) > 0;
    }

    @Override
    public List<Message> findFeed(int subscriberId, OffsetDateTime beforeTs, Integer beforeId, int limit) {
        if (beforeTs == null) {
            return jdbcTemplate.query(Constants.GET_FEED,
                    (rs, rowNum) -> daoHelper.mapMessage(rs), subscriberId, limit);
        }
        return jdbcTemplate.query(Constants.GET_FEED_BEFORE,
                (rs, rowNum) -> daoHelper.mapMessage(rs), subscriberId, beforeTs, beforeId, limit);
    }
}
