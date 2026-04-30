# Notification Service

Уведомления (Telegram бот, внутренние сообщения).

## 🔧 Технологии
- Java 21
- Spring Boot 2.7.18
- Spring WebFlux
- PostgreSQL
- Liquibase
- Telegram Bot API
- Eureka Client

## 📦 Порт
9920

## 🗄️ База данных
- Имя: cd_notification
- Пользователь: postgres
- Пароль: password

## 🤖 Telegram Bot
- Username: @checkdev_bot
- Профиль: develop (логи в консоль)

### Команды бота

| Команда | Описание |
|---------|----------|
| /start | Список команд |
| /new | Регистрация |
| /check | Связанный аккаунт |
| /forget | Восстановить пароль |
| /notify | Подписаться на уведомления |
| /unnotify | Отписаться от уведомлений |
| /bind | Привязать аккаунт |
| /unbind | Отвязать аккаунт |

## 📚 API Endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| GET | /messages/actual/{userId} | Непрочитанные сообщения |
| POST | /messages/newInterview | Оповещение о новом интервью |
| POST | /notification/topic/ | Рассылка подписчикам темы |
| POST | /feedback/interview | Уведомление об отзыве |

## 📄 Swagger
http://localhost:9920/swagger-ui/index.html

## 🔗 Eureka
Регистрируется под именем notification

## 🚀 Запуск
```bash
mvn spring-boot:run