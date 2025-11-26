-- =========================
-- INSERT USERS
-- =========================
INSERT INTO users (id, username, password, role)
VALUES
    (1, 'admin', '$2a$10$KKUE4t2VdmSjMbsigAnrg.jZEZiqQQv/ZlI9ngqDAnsKB4McL23Jq', 'ADMIN'),  -- lưu password đã mã hóa bằng BCrypt
    (2, 'kien', '$2a$10$4twmHfImIae9Nu.cbq1jU.A5pjBsWZbnXBbqKGPI84Z62p7jB.LiG', 'USER'),
    (3, 'an', '$2a$10$5o4erI.wUzIjnUgxQ29ce.utsKnQnqWwZysHLJolNlJnG6VcuBxTa.', 'USER'),
    (4, 'linh', '$2a$10$5o4erI.wUzIjnUgxQ29ce.utsKnQnqWwZysHLJolNlJnG6VcuBxTa.', 'USER');

-- =========================
-- INSERT TASKS
-- =========================
INSERT INTO tasks (id, title, description, completed, created_at, user_id)
VALUES
    (1, 'Viết API login', 'Tạo API đăng nhập cho hệ thống', false, NOW(), 1),
    (2, 'Tạo giao diện dashboard', 'Thiết kế UI màn hình chính', false, NOW(), 2),
    (3, 'Fix bug task service', 'Lỗi null pointer trong TaskService', true, NOW(), 1),
    (4, 'Task 4', 'Mô tả task 4', false, NOW(), 1),
    (5, 'Task 5', 'Mô tả task 5', true, NOW(), 2),
    (6, 'Task 6', 'Mô tả task 6', false, NOW(), 3),
    (7, 'Task 7', 'Mô tả task 7', true, NOW(), 4),
    (8, 'Task 8', 'Mô tả task 8', false, NOW(), 1),
    (9, 'Task 9', 'Mô tả task 9', true, NOW(), 2),
    (10, 'Task 10', 'Mô tả task 10', false, NOW(), 3),
    (11, 'Task 11', 'Mô tả task 11', true, NOW(), 4),
    (12, 'Task 12', 'Mô tả task 12', false, NOW(), 1),
    (13, 'Task 13', 'Mô tả task 13', true, NOW(), 2),
    (14, 'Task 14', 'Mô tả task 14', false, NOW(), 3),
    (15, 'Task 15', 'Mô tả task 15', true, NOW(), 4),
    (16, 'Task 16', 'Mô tả task 16', false, NOW(), 1),
    (17, 'Task 17', 'Mô tả task 17', true, NOW(), 2),
    (18, 'Task 18', 'Mô tả task 18', false, NOW(), 3),
    (19, 'Task 19', 'Mô tả task 19', true, NOW(), 4),
    (20, 'Task 20', 'Mô tả task 20', false, NOW(), 1);