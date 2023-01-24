CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(32) UNIQUE NOT NULL,
    password VARCHAR(64)        NOT NULL,
    role     VARCHAR(32)        NOT NULL default 'USER'
);

CREATE INDEX name_index ON users USING HASH (name);