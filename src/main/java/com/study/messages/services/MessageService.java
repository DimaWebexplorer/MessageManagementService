package com.study.messages.services;

import com.study.messages.dao.MessagesRepository;
import com.study.messages.dto.Message;
import com.study.messages.dto.MessagePage;
import com.study.messages.exceptions.ApiException;
import com.study.messages.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessagesRepository messagesRepository;

    public Message createMessage(String text, int authorId) {
        return messagesRepository.createMessage(text.trim(), authorId);
    }

    public Message getMessage(int id) {
        return messagesRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    public MessagePage getAuthorMessages(int authorId, OffsetDateTime beforeTs, Integer beforeId, int limit) {
        List<Message> rows = messagesRepository.findByAuthor(authorId, beforeTs, beforeId, limit + 1);
        return toPage(rows, limit);
    }

    public Message updateMessage(int id, String text, int currentUserId, boolean isAdmin) {
        Message existing = getMessage(id);
        checkCanModify(existing, currentUserId, isAdmin);
        return messagesRepository.updateMessage(id, text.trim())
                .orElseThrow(() -> new ApiException(ErrorCode.MESSAGE_NOT_FOUND));
    }

    public void deleteMessage(int id, int currentUserId, boolean isAdmin) {
        Message existing = getMessage(id);
        checkCanModify(existing, currentUserId, isAdmin);
        if (!messagesRepository.deleteMessage(id)) {
            throw new ApiException(ErrorCode.MESSAGE_NOT_FOUND);
        }
    }

    public MessagePage getFeed(int subscriberId, OffsetDateTime beforeTs, Integer beforeId, int limit) {
        List<Message> rows = messagesRepository.findFeed(subscriberId, beforeTs, beforeId, limit + 1);
        return toPage(rows, limit);
    }

    private void checkCanModify(Message message, int currentUserId, boolean isAdmin) {
        if (!isAdmin && !message.getAuthorId().equals(currentUserId)) {
            throw new ApiException(ErrorCode.NOT_AUTHOR);
        }
    }

    private MessagePage toPage(List<Message> rows, int limit) {
        boolean hasMore = rows.size() > limit;
        List<Message> items = hasMore ? rows.subList(0, limit) : rows;
        if (!hasMore || items.isEmpty()) {
            return new MessagePage(items, null, null);
        }
        Message last = items.getLast();
        return new MessagePage(items, last.getCreatedAt(), last.getId());
    }
}
