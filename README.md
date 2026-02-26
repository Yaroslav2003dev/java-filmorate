# java-filmorate

## Диаграмма базы данных filmorate

![ER DataBase filmorate](src/main/resources/img.png)

### Вывод всех пользователей
SELECT * FROM User;

### Поиск взаимной дружбы между двумя конкретными пользователями
SELECT * FROM FriendRequest fr1
JOIN FriendRequest fr2 ON fr1.userOne_id=fr2.userTwo_id AND fr1.userTwo_id=fr2.userOne_id
WHERE fr1.userOne_id=<идентификатор пользователя> AND fr1.userTwo_id=<идентификатор другого пользователя>;

### Поиск общих друзей у двух пользователей
SELECT * FROM (SELECT fr1.userTwo_id FROM FriendRequest fr1
JOIN FriendRequest fr2 ON fr1.userOne_id=fr2.userTwo_id AND fr1.userTwo_id=fr2.userOne_id
WHERE fr1.userOne_id=<идентификатор пользователя>) t WHERE t.userTwo_id IN (SELECT fr1.userTwo_id FROM FriendRequest fr1
JOIN FriendRequest fr2 ON fr1.userOne_id=fr2.userTwo_id AND fr1.userTwo_id=fr2.userOne_id
WHERE fr1.userOne_id=<идентификатор другого пользователя>);

### Вывод всех фильмов
SELECT * FROM Film;

### Получение ТОП 10 фильмов по поставленным лайкам
SELECT f.name  FROM Film f LEFT JOIN LikeFilm lf ON f.film_id=lf.film_id
GROUP BY f.film_id, f.name ORDER BY COUNT(lf.user_id) DESC LIMIT 10;




