# HiberRedisDocker

Проект для работы с базой данных MySQL (через Hibernate) и Redis (через Lettuce) 

## 📚 Что делает проект

1. Загружает данные о городах из MySQL через Hibernate
2. Преобразует их в объект `CityCountry` с JSON-сериализацией
3. Сохраняет в Redis с ключом по ID города
4. Позволяет сравнить скорость доступа к данным из Redis и MySQL

## 🛠 Технологии

| Технология | Назначение |
|------------|-----------|
| **Hibernate 5.6.14.Final** | ORM для работы с MySQL |
| **Redis + Lettuce** | Быстрое хранение данных в памяти |
| **Jackson** | JSON-сериализация |
| **JUnit 5** | Тестирование |
| **Testcontainers** | Автоматический запуск Redis в Docker |

## 📦 Требования

- **JDK 18+** 
- **Maven 3.8+**
- **Docker Desktop** (для запуска Redis и Testcontainers)

## 🚀 Запуск

### Шаг 1: Запустите Redis

bash docker run -d --name redis -p 6379:6379 redis:7-alpine

Проверьте: bash docker exec -it redis redis-cli ping
Ответ должен быть PONG


### Шаг 2: Запустите MySQL и загрузите БД `world`

1. Убедитесь, что MySQL запущен и доступен по `localhost:3306`
2. Проверьте наличие БД `world` и таблиц:
   sql SHOW DATABASES; USE world; SHOW TABLES; SELECT COUNT(*) FROM city;

### Шаг 3: Соберите и запустите проект   

bash mvn clean compile mvn exec:java -Dexec.mainClass="world.Main"

Ожидаемый вывод:
Connected to Redis Saved to Redis: 1 Saved to Redis: 2 ... Redis: 45 ms MySQL: 120 ms

## 🧪 Запуск тестов

bash mvn test -Dtest=RedisIntegrationTest

⚠️ **Если Docker не запущен**, тесты пропустятся или упадут с ошибкой `Could not find a valid Docker environment`.


---

## ⚙️ Конфигурация

| Параметр | Значение по умолчанию | Описание |
|----------|----------------------|----------|
| `REDIS_HOST` | `localhost` | Хост Redis (можно задать через `System.setProperty`) |
| `REDIS_PORT` | `6379` | Порт Redis |
| `DB_URL` | `jdbc:mysql://localhost:3306/world` | URL подключения к MySQL |
| `DB_USER` | `root` | Пользователь MySQL |
| `DB_PASS` | `root` | Пароль MySQL |

---

## ❗ Ошибки и решения

| Ошибка | Решение |
|--------|---------|
| `Redis is not available` | Запустите Redis: `docker run -d --name redis -p 6379:6379 redis:7-alpine` |
| `Unknown column 'CountryCode'` | Убедитесь, что в `City.java` правильное имя колонки: `@JoinColumn(name = "CountryCode")` |
| `LazyInitializationException` | В `fetchData()` используется `join fetch c.country co left join fetch co.languages` |
| `Could not find a valid Docker environment` | Запустите Docker Desktop или используйте `TESTCONTAINERS_RYUK_DISABLED=true` |

---

