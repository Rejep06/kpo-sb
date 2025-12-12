# Антиплагиат — микросервисное приложение (Spring Boot, PostgreSQL)

## Описание

Система предназначена для хранения студенческих работ и формирования отчётов
по проверке на заимствования (антиплагиат).

Архитектура микросервисная:

- **API Gateway**
- **File Storage Service** — хранение файлов и метаданных.
- **File Analysis Service** — анализ, хранение и выдача отчётов.

Каждый бизнес‑микросервис имеет собственную БД PostgreSQL.

## Архитектура и взаимодействие

### Компоненты

1. **API Gateway**
    - Принимает внешние запросы от студентов и преподавателей.
    - Обрабатывает сценарий «сдать работу»:
        - принимает `multipart/form-data` с файлом;
        - кодирует файл в Base64;
        - отправляет JSON в File Storage Service;
        - после успешного сохранения запускает анализ в File Analysis Service.
    - Предоставляет эндпоинт для получения аналитики по контрольной работе.

2. **File Storage Service**
    - Отвечает за сохранение файлов на диск и запись метаданных в PostgreSQL.
    - Предоставляет внутренние эндпоинты:
        - `POST /internal/files` — сохранить файл (Base64);
        - `GET /internal/files/{id}/content` — получить содержимое (Base64);
        - `GET /internal/files/{id}/metadata` — получить метаданные файла.
    - Не знает ничего о логике анализа/плагиата.

3. **File Analysis Service**
    - Получает от Gateway команду анализа (`fileId`, `studentId`, `assignmentId`).
    - Забирает текст работы из File Storage Service.
    - Вычисляет хэш SHA‑256 содержимого.
    - Определяет плагиат:  
      если существует более ранняя сдача **другим** студентом
      с таким же хэшем и тем же `assignmentId`, помечает отчёт как плагиат.
    - Хранит отчёты в собственной БД.
    - Выдаёт:
        - отчёт по конкретной сдаче;
        - список отчётов по одной контрольной работе;
        - URL для облака слов через QuickChart API.

### Пользовательские сценарии

#### 1. Сдача работы студентом

**Запрос:**

```http
POST /api/v1/works
Content-Type: multipart/form-data

studentId=student123
assignmentId=42
file=@report.txt

Технический сценарий:
Gateway принимает файл и метаданные.


Конвертирует файл в Base64 и вызывает:

 POST file-storage-service/internal/files
Content-Type: application/json

{
  "studentId": "student123",
  "assignmentId": 42,
  "fileName": "report.txt",
  "contentType": "text/plain",
  "contentBase64": "..."
}


File Storage Service сохраняет файл на диск и запись в БД, возвращает:

 {
  "id": 10,
  "studentId": "student123",
  "assignmentId": 42,
  "originalFileName": "report.txt",
  "sizeBytes": 1234,
  "createdAt": "2025-05-01T12:34:56"
}


Gateway вызывает File Analysis Service:

 POST file-analysis-service/internal/reports
Content-Type: application/json

{
  "fileId": 10,
  "studentId": "student123",
  "assignmentId": 42
}


File Analysis:


забирает текст через GET file-storage-service/internal/files/10/content;


считает хэш текста;


ищет более ранние сдачи с тем же assignmentId и хэшем;


создаёт отчёт и сохраняет его в БД.


Gateway возвращает студенту JSON:

 {
  "submission": { ... },
  "report": {
    "id": 5,
    "fileId": 10,
    "studentId": "student123",
    "assignmentId": 42,
    "status": "COMPLETED",
    "plagiarismDetected": false,
    "baseFileId": null,
    "createdAt": "...",
    "updatedAt": "..."
  }
}


Если File Analysis Service недоступен, Gateway всё равно вернёт информацию о сохранении файла, а report будет null. Это демонстрирует обработку сбоя одного из микросервисов.
2. Аналитика по контрольной работе (преподаватель)
Запрос:
GET /api/v1/works/42/reports

Технический сценарий:
Gateway вызывает:

 GET file-analysis-service/internal/reports?assignmentId=42


File Analysis Service возвращает список отчётов:

 [
  {
    "reportId": 5,
    "fileId": 10,
    "studentId": "student123",
    "status": "COMPLETED",
    "plagiarismDetected": false
  },
  {
    "reportId": 6,
    "fileId": 11,
    "studentId": "student999",
    "status": "COMPLETED",
    "plagiarismDetected": true
  }
]


Gateway отдаёт это наружу без изменений.


3. Облако слов (на 10 баллов)
Запрос преподавателя:
GET /internal/reports/{reportId}/word-cloud

File Analysis Service:
Находит отчёт и файл.


Получает текст работы из File Storage Service.


Строит URL для API https://quickchart.io/wordcloud?c=....


Возвращает строку с этим URL клиенту.


Клиент может отобразить картинку по этой ссылке.
Алгоритм определения плагиата
Для каждой новой работы:


получаем текст (UTF-8),


считаем хэш SHA‑256 (строка в hex).


В базе отчётов ищем записи с таким же:


assignmentId,


contentHash.


Если есть запись, у которой:


studentId другой, чем у текущего отчёта,


createdAt < createdAt текущего отчёта,
 то считаем, что новый отчёт — плагиат, заполняем:


plagiarismDetected = true,


baseFileId = fileId найденной работы.


Иначе plagiarismDetected = false.


Такой алгоритм соответствует условию:
плагиат присутствует, если существует более ранняя сдача (другим студентом) присланной работы.
Запуск

# 2. Запустить с помощью docker-compose
docker compose up --build

После запуска:
Gateway: http://localhost:8080


Swagger UI:


http://localhost:8080/swagger-ui/index.html


http://localhost:8081/swagger-ui/index.html


http://localhost:8082/swagger-ui/index.html


