INSERT INTO postgres.users (name, password, role)
VALUES ('anonymous_user', 'anonymous_user_password', 'VISITOR');
INSERT INTO postgres.users (name, password)
VALUES ('@simon', 'password');