# ДЗ-4 «Гоzон»: Orders + Payments (RabbitMQ + Transactional Outbox/Inbox + Redis cache)

Проект реализует требования из ТЗ (Orders Service + Payments Service, асинхронная оплата заказа, гарантии at-least-once и семантика effectively exactly once при списании денег).

## Архитектура

**Orders Service**
- REST API: создать заказ, список заказов, статус заказа.
- При создании заказа пишет:
  1) `orders` (сам заказ со статусом `NEW`)
  2) `outbox_message` (команда `PaymentRequested`)
  в **одной транзакции** (Transactional Outbox).
- Отдельный планировщик (`OutboxRelay`) публикует outbox-сообщения в RabbitMQ.

**Payments Service**
- REST API: создать счёт (не более 1 на пользователя), пополнить, посмотреть баланс.
- Консьюмер RabbitMQ получает `PaymentRequested` и делает:
  1) записывает сообщение в `inbox_message` (Transactional Inbox)
  2) выполняет списание (или фиксирует ошибку)
  3) пишет `payment_operation` (уникально по `order_id`) => «деньги не спишутся дважды»
  4) пишет `outbox_message` с `PaymentResult`
  всё в **одной транзакции**.
- Планировщик `OutboxRelay` публикует `PaymentResult` в RabbitMQ.

**Idempotency / exactly-once**
- Доставка RabbitMQ: **at-least-once** (manual ack).
- «Effectively exactly once» для списания достигается комбинацией:
  - **Transactional Inbox** (уникальный `message_id` входящего сообщения)
  - **idempotent бизнес-логика** в Payments Service: таблица `payment_operation` имеет `UNIQUE(order_id)`

**Кэширование**
- В обоих сервисах включён Spring Cache поверх Redis:
  - Payments: `balanceByUser`
  - Orders: `ordersByUser`, `orderById`
- TTL по умолчанию: 30 секунд (`spring.cache.redis.time-to-live`).

## Запуск через Docker Compose

### Требования
- Docker + Docker Compose.

### Сборка и старт
Из корня проекта:
```bash
docker compose up --build
```

Поднимаются контейнеры:
- PostgreSQL для orders (порт наружу `5433`)
- PostgreSQL для payments (порт наружу `5434`)
- RabbitMQ + management UI (AMQP `5672`, UI `15672`)
- Redis (`6379`)
- orders-service (2 инстанса, наружу проброшен только `orders-service-1` на `8081`)
- payments-service (`8082`)
- api-gateway (`8080`) – единая точка входа (routing only)
- frontend (статическая страница, доступна через api-gateway)

### Swagger UI (как раньше, напрямую)
- Orders Service (instance 1): http://localhost:8081/swagger-ui/index.html
- Payments Service: http://localhost:8082/swagger-ui/index.html

### API Gateway
- UI (демо WebSocket + push): http://localhost:8080/
- Orders API через gateway: http://localhost:8080/api/orders
- Payments API через gateway: http://localhost:8080/api/accounts

### RabbitMQ UI
- http://localhost:15672
- логин/пароль: `guest/guest`

## Как тестировать сценарий создания заказа (асинхронная оплата)

> Везде передаём `X-User-Id`.

### 1) Создать счёт (через gateway)
```bash
curl -X POST "http://localhost:8080/api/accounts" \
  -H "X-User-Id: user-1"
```

### 2) Пополнить
```bash
curl -X POST "http://localhost:8080/api/accounts/topup" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-1" \
  -d '{"amount": 10000}'
```

### 3) Создать заказ (запустит оплату асинхронно)
```bash
curl -X POST "http://localhost:8080/api/orders" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-1" \
  -d '{"amount": 2500, "description": "New Year gift"}'
```

В ответе будет `id` заказа. Сразу после создания статус будет `NEW`.

### 4) Проверить статус заказа
```bash
curl "http://localhost:8080/api/orders/{orderId}" \
  -H "X-User-Id: user-1"
```

## Реальное отслеживание статуса заказа (WebSocket + push)

Реализовано по ТЗ: клиент подключается к WebSocket после создания заказа; при смене статуса сервер отправляет уведомление.

### Как проверить в браузере
1) Откройте UI: http://localhost:8080/
2) Нажмите «Создать счёт» → «Пополнить» → «Создать заказ»
3) Страница автоматически подключится к WebSocket и покажет всплывающее уведомление (toast). При желании можно нажать «Разрешить уведомления» для Notification API.

### Как проверить вручную (без UI)
Подключитесь любым WebSocket-клиентом к:
`ws://localhost:8080/ws/orders?userId=user-1&orderId=<uuid>`

### Масштабирование (несколько инстансов бэкенда)
В docker-compose запущены **2 инстанса Orders Service**. 
Уведомления корректно доставляются даже если обработка события оплаты происходит на другом инстансе:
- instance, обработавший событие, публикует обновление статуса в Redis Pub/Sub
- **все инстансы** получают сообщение и пушат его своим WebSocket-клиентам

Через короткое время (после обработки очереди) статус станет:
- `FINISHED` если денег хватило
- `CANCELLED` если денег не хватило / нет счёта

## Настройка конфигурации

Конфигурация сделана через `application.yml` + переменные окружения.

### Orders Service (orders-service/src/main/resources/application.yml)
Основные переменные:
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `RABBIT_HOST`, `RABBIT_PORT`, `RABBIT_USER`, `RABBIT_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `SERVER_PORT`

Outbox-планировщик:
- `outbox.publish.fixed-delay` — задержка между итерациями
- `outbox.publish.batch-size` — размер пачки
- `outbox.publish.lock-timeout-seconds` — через сколько считать IN_PROGRESS «протухшим»

### Payments Service (payments-service/src/main/resources/application.yml)
Аналогично Orders + те же настройки Outbox.

### Изменить TTL кэша
Например, поставить 5 секунд:
```yaml
spring:
  cache:
    redis:
      time-to-live: 5s
```

### Отключить кэш
```yaml
spring:
  cache:
    type: none
```

## Замечания по денежным суммам
`amount` — **целое число** в «минимальных единицах» (копейки/центы), тип `long`.
Так проще обеспечить корректность без float/double.

## Структура БД

Orders DB:
- `orders`
- `outbox_message`
- `inbox_message`

Payments DB:
- `accounts` (уникально по `user_id`)
- `payment_operation` (уникально по `order_id`)
- `outbox_message`
- `inbox_message`
