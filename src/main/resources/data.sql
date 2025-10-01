DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM teams;
DELETE FROM events;
DELETE FROM articles;
DELETE FROM events_participants;
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE events_id_seq RESTART WITH 1;
ALTER SEQUENCE teams_id_seq RESTART WITH 1;
ALTER SEQUENCE articles_id_seq RESTART WITH 1;
INSERT INTO users (login, password, email)
VALUES ('rin', '$2a$10$LIfdmZMS.OP4oUaq9qgHIeWAvFucyj7Mg7wtksKzAdjnFTCp.fdd6', 'ariadna.kolupaeva@yandex.ru');
INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_ADMIN');