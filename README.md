# Movie Parser
Это тестовый проект для асинхронного подсчета среднего значения фильмов по жанру.
## Общая задача:
1. Написать java-client для получения информации о фильмах с сервера
https://easy.test-assignment-a.loyaltyplant.net/​
2. Запросы отправлять на endpoint-ы:
a. 3/discover/movie?api_key=${key}&page=${page}
b. 3/genre/movie/list?api_key=${key}
3. В качестве key использовать: 72b56103e43843412a992a8d64bf96e9

## Реализована следующая функциональность:
○ Получение средней оценки по всем фильмам определё нного жанра (на вход id
жанра).
○ Получение данных должно выполняться в фоновом режиме.
○ В процессе подсчё та должна быть возможность узнать о текущем прогрессе.
○ Должна быть возможность остановить подсчёт.
