DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM teams;
DELETE FROM events;
DELETE FROM team_requests;
DELETE FROM training_attendances;
DELETE FROM rates;
DELETE FROM methodology_blocks;
DELETE FROM methodologies;
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE events_id_seq RESTART WITH 1;
ALTER SEQUENCE teams_id_seq RESTART WITH 1;
ALTER SEQUENCE methodology_blocks_id_seq RESTART WITH 1;
ALTER SEQUENCE methodologies_id_seq RESTART WITH 1;
ALTER SEQUENCE team_requests_id_seq RESTART WITH 1;
ALTER SEQUENCE training_attendances_id_seq RESTART WITH 1;
ALTER SEQUENCE rates_id_seq RESTART WITH 1;
INSERT INTO users (username, password, email, join_date, last_online, faceit_nickname)
VALUES
('rin', '$2a$10$LIfdmZMS.OP4oUaq9qgHIeWAvFucyj7Mg7wtksKzAdjnFTCp.fdd6', 'ariadna.kolupaeva@yandex.ru', NOW(), NOW(), 'rinchik0'),
('mur', '$2a$10$6OWMdxxGQKFX5u6LzFVmMOtdsmz6ljnuDkyGYvLXvLAN4wmae16j2', 'mur@gmail.com', NOW(), NOW(), 'murka');
INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_ADMIN'),
(2, 'ROLE_ADMIN'),
(1, 'ROLE_GUEST'),
(2, 'ROLE_GUEST');