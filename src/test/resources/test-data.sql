-- USERS
INSERT INTO USERS (id, username, password, role) VALUES (1, 'admin', '123', 'ADMIN');
INSERT INTO USERS (id, username, password, role) VALUES (2, 'kien', '123456', 'USER');

-- TASKS
INSERT INTO TASKS (title, description, completed, created_at, user_id)
VALUES ('Task 1', 'Description 1', FALSE, CURRENT_TIMESTAMP, 1);

INSERT INTO TASKS (title, description, completed, created_at, user_id)
VALUES ('Task 2', 'Description 2', FALSE, CURRENT_TIMESTAMP, 2);

INSERT INTO TASKS (title, description, completed, created_at, user_id)
VALUES ('Task 3', 'Description 3', TRUE, CURRENT_TIMESTAMP, 1);
