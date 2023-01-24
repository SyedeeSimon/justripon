CREATE TABLE IF NOT EXISTS quizzes
(
    id             SERIAL PRIMARY KEY,
    url_path       VARCHAR(6) UNIQUE NOT NULL,
    title          VARCHAR(512)      NOT NULL,
    draft          BOOLEAN           NOT NULL,
    author_user_id BIGINT            NOT NULL,
    questions      text
);

CREATE INDEX author_user_id_index ON quizzes USING HASH (author_user_id);
CREATE INDEX url_path_index ON quizzes USING HASH (url_path);
