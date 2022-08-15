CREATE TABLE IF NOT EXISTS USERS
(
    id       long AUTO_INCREMENT PRIMARY KEY,
    name     varchar(200),
    login    varchar(200),
    email    varchar(200),
    birthday date
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP_STATUS
(
    id   long AUTO_INCREMENT PRIMARY KEY,
    name varchar(200)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP
(
    user_id   long REFERENCES USERS (id) ON DELETE CASCADE,
    friend_id long REFERENCES USERS (id) ON DELETE CASCADE,
    status_id long DEFAULT 2,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (status_id) REFERENCES FRIENDSHIP_STATUS (id)
);

CREATE TABLE IF NOT EXISTS MPA
(
    id          long AUTO_INCREMENT PRIMARY KEY,
    name        varchar(200),
    description varchar(200)
);

CREATE TABLE IF NOT EXISTS FILMS
(
    id           long AUTO_INCREMENT PRIMARY KEY,
    name         varchar(200),
    description  varchar(200),
    duration     long,
    release_date date,
    rate         long,
    mpa_id       long REFERENCES MPA (id)
);

CREATE TABLE IF NOT EXISTS LIKES
(
    film_id long REFERENCES FILMS (id) ON DELETE CASCADE,
    user_id long REFERENCES USERS (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS GENRES
(
    id   long AUTO_INCREMENT PRIMARY KEY,
    name varchar(200)
);

CREATE TABLE IF NOT EXISTS FILMS_GENRES
(
    film_id  long REFERENCES FILMS (id) ON DELETE CASCADE,
    genre_id long REFERENCES GENRES (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

