## ER-диаграмма БД Filmorate

https://app.quickdatabasediagrams.com/#/d/UlRdt2

![Untitled (1)](https://github.com/Grmelik/add-database/blob/main/Schema.jpg)

**_База данных хранит в себе данные о фильмах, их жанрах и рейтингах, а также о пользователях, их друзьях и лайках._**

- Таблица **User** хранит данные о пользователях (логин, имя, email, день рождения).

- Таблица **Friends** хранит данные о друзьях пользователя и статусах их дружбы.

- Таблица **Statuses** является справочником и хранит статусы дружбы.

- Таблица **Likes** хранит лайки, поставленные пользователями фильмам.

- Таблица **Films** хранит данные о фильмах (наименование, описание, дата релиза, продолжительность, рейтинг).

- Таблица **Genres** хранит данные о жанрах фильма.

- Таблица **GenresNames** является справочником и хранит наименования жанров.

- Таблица **Ratings** является справочником и хранит наименования рейтингов.

**Примеры запросов:**

```sql
SELECT * FROM films
```

```sql
SELECT * FROM user
```

```sql
WITH t AS (
SELECT g.FilmID, 
       gn.GenreName
  FROM Genres g, GenreNames gn
 WHERE g.GenreID = gn.GenreID)

SELECT FilmName,
       ReleaseDate,
       Duration,
       (SELECT STRING_AGG(GenreName, ', ' ORDER BY GenreName) FROM t WHERE t.FilmID = f.FilmID) AS Genres,
       Description,
       RatingName
  FROM Films f, Ratings r
 WHERE f.RatingID = r.RatingID
```
