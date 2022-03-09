CREATE TABLE users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  login TEXT NOT NULL UNIQUE,
  nickname TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL
);

INSERT INTO users (login, nickname, password) VALUES
("login1", "first_user", "password1"),
("login2", "second_user", "password2"),
("login3", "third_user", "password3"),
("login4", "forth_user", "password4");