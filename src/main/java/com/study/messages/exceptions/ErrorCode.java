package com.study.messages.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    VALIDATION_ERROR(400, "Проверьте поля"),
    EMPTY_TEXT(400, "Сообщение не может быть пустым"),
    INVALID_LIMIT(400, "Можно загрузить не больше 50 сообщений"),
    SELF_SUBSCRIPTION(400, "Нельзя подписаться на себя"),
    NOT_PRODUCER(400, "На этого пользователя нельзя подписаться"),
    UNAUTHORIZED(401, "Войдите в аккаунт"),
    FORBIDDEN(403, "Недостаточно прав"),
    PRODUCER_ONLY(403, "Писать сообщения могут только авторы"),
    NOT_AUTHOR(403, "Можно менять только свои сообщения"),
    NOT_FOUND(404, "Не найдено"),
    USER_NOT_FOUND(404, "Пользователь не найден"),
    MESSAGE_NOT_FOUND(404, "Сообщение не найдено"),
    SUBSCRIPTION_NOT_FOUND(404, "Вы не подписаны на этого автора"),
    ALREADY_SUBSCRIBED(409, "Вы уже подписаны на этого автора"),
    INTERNAL_ERROR(500, "Что-то пошло не так, попробуйте позже"),
    SERVICE_UNAVAILABLE(502, "Сервис временно недоступен");

    private final int status;
    private final String message;
}
