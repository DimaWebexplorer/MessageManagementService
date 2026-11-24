package com.study.messages.validators;

import com.study.messages.dto.MessageRequest;
import com.study.messages.exceptions.ApiException;
import com.study.messages.exceptions.ErrorCode;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RequestValidator {

    private static final int MAX_TEXT_LENGTH = 4000;
    private static final int MAX_LIMIT = 50;

    public void validateId(Integer id, String field) {
        if (id == null || id <= 0) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, Map.of(field, "Нужен положительный id"));
        }
    }

    public void validateMessageText(MessageRequest body) {
        if (body == null || body.getText() == null || body.getText().isBlank()) {
            throw new ApiException(ErrorCode.EMPTY_TEXT);
        }
        if (body.getText().trim().length() > MAX_TEXT_LENGTH) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR,
                    Map.of("text", "Сообщение не длиннее " + MAX_TEXT_LENGTH + " символов"));
        }
    }

    public OffsetDateTime validateFeedParams(int limit, String beforeTsRaw, Integer beforeId) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ApiException(ErrorCode.INVALID_LIMIT);
        }
        boolean hasTs = beforeTsRaw != null;
        boolean hasId = beforeId != null;
        if (hasTs != hasId) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR,
                    Map.of("cursor", "Укажите оба параметра: before_ts и before_id"));
        }
        if (!hasTs) {
            return null;
        }
        Map<String, String> errors = new LinkedHashMap<>();
        OffsetDateTime parsedTs = null;
        try {
            parsedTs = OffsetDateTime.parse(beforeTsRaw.trim());
        } catch (DateTimeParseException e) {
            errors.put("before_ts", "Нужна дата в формате ISO-8601, например 2026-09-27T13:00:00+05:00");
        }
        if (beforeId <= 0) {
            errors.put("before_id", "Нужен положительный id");
        }
        throwIfInvalid(errors);
        return parsedTs;
    }

    public void validateFollowersParams(int afterId, int limit) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (afterId < 0) {
            errors.put("after_id", "after_id должен быть не меньше 0");
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            errors.put("limit", "От 1 до " + MAX_LIMIT + " подписчиков");
        }
        throwIfInvalid(errors);
    }

    public void validateNotSelfSubscription(int subscriberId, int producerId) {
        if (subscriberId == producerId) {
            throw new ApiException(ErrorCode.SELF_SUBSCRIPTION);
        }
    }

    private void throwIfInvalid(Map<String, String> errors) {
        if (!errors.isEmpty()) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, errors);
        }
    }
}
