CREATE TABLE IF NOT EXISTS Mpa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS Genre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS Film (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    release_date DATE NOT NULL,
    duration INT,
    mpa_id BIGINT,

    CONSTRAINT fk_film_mpa
        FOREIGN KEY (mpa_id)
        REFERENCES Mpa(id)
        ON DELETE RESTRICT
);
CREATE TABLE IF NOT EXISTS FilmGenre (
    film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,

    PRIMARY KEY (film_id, genre_id),

    CONSTRAINT fk_filmgenre_film
        FOREIGN KEY (film_id)
        REFERENCES Film(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_filmgenre_genre
        FOREIGN KEY (genre_id)
        REFERENCES Genre(id)
        ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    login VARCHAR(100) UNIQUE,
    name VARCHAR(255),
    birthday DATE
);
CREATE TABLE IF NOT EXISTS like_film (
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, film_id),

    CONSTRAINT fk_like_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_like_film
        FOREIGN KEY (film_id)
        REFERENCES film(id)
        ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS friend_request (
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,

    CONSTRAINT pk_friend_request PRIMARY KEY (sender_id, receiver_id),
    CONSTRAINT fk_friend_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);