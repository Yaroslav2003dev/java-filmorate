MERGE INTO Mpa (name) KEY(name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');
MERGE INTO Genre (id, name) KEY(id) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');