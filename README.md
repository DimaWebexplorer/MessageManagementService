# MessageManagementService

Сервис сообщений, подписок и ленты. Java 21, Spring Boot 3.5, PostgreSQL.

## запуск

```bash
docker compose up -d
./gradlew bootRun
```

Порт 9001, база `messages_db` на порту 5433. Для проверки подписок нужен запущенный UserManagementService на порту 9000.

## ручки

| метод | путь | что делает |
|---|---|---|
| POST | /api/v1/messages | создать сообщение |
| GET | /api/v1/messages/{id} | сообщение по id |
| GET | /api/v1/messages?author_id=&limit=&before_ts=&before_id= | сообщения автора |
| PATCH | /api/v1/messages/{id} | изменить сообщение |
| DELETE | /api/v1/messages/{id} | удалить сообщение |
| GET | /api/v1/messages/feed?limit=&before_ts=&before_id= | лента подписок |
| POST | /api/v1/subscriptions | подписаться на producer |
| DELETE | /api/v1/subscriptions/{producer_id} | отписаться |
| GET | /api/v1/subscriptions/my | мои подписки |
| GET | /api/v1/subscriptions/{producer_id}/check | проверка подписки |
| GET | /api/v1/subscribers/{producer_id}/followers?after_id=&limit= | подписчики автора |
| GET | /api/v1/subscribers/{user_id}/stats | статистика подписок |
