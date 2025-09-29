-- V1__Initial_Schema.sql
-- Creation and population of the basic schema for Task Manager

-- 1. Creating the users table (users)
-- The table name 'users' corresponds to @Table(name = "users") in the User entity.
CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY, -- We use BIGSERIAL to match Long in Java
    username      VARCHAR(50) NOT NULL UNIQUE, -- Corresponds to @Size(max=50) and @Column(unique=true)
    email         VARCHAR(255) NOT NULL UNIQUE, -- Added email field, corresponds to @Email and @Column(unique=true)
    password      VARCHAR(255) NOT NULL, -- Corresponds to @Column

    -- The role field stores the string 'USER' or 'ADMIN', which corresponds to @Enumerated(EnumType.STRING)
    role          VARCHAR(50) NOT NULL,

    -- Fields for tracking dates, correspond to @PrePersist and @PreUpdate
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    is_enabled    BOOLEAN NOT NULL DEFAULT TRUE -- Corresponds to 'private boolean enabled = true'
);

-- 2. Creating the tasks table (tasks)
-- The table name 'tasks' corresponds to @Table(name = "tasks") in the Task entity.
CREATE TABLE IF NOT EXISTS tasks (
    id            BIGSERIAL PRIMARY KEY, -- BIGSERIAL for Long
    title         VARCHAR(100) NOT NULL, -- Corresponds to @Size(max=100)
    description   VARCHAR(500), -- Corresponds to @Size(max=500)

    -- The status field stores a string, which corresponds to @Enumerated(EnumType.STRING)
    status        VARCHAR(50) NOT NULL,

    -- Foreign key referencing the correct 'users' table
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- ON DELETE CASCADE will delete user's tasks when the user is deleted

    -- Fields for tracking dates
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- 3. Creating indexes to speed up searches
-- Indexes are critical for fields that will be filtered on.
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks (status);
CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks (user_id);

-- 4. Populating with test data (optional, but useful for development)
-- Password for everyone: 'password' (encoded with BCrypt)
-- Important: this block can be moved to a separate migration file, for example, V2__Test_Data.sql

-- Create users
-- Note: we are inserting the string values 'USER' and 'ADMIN' into the role field
-- This syntax will work in both databases.
-- We assume the database is clean and there will be no conflicts.
INSERT INTO users (username, email, password, role, created_at, updated_at) VALUES
('user', 'user@user.ru', '$2a$10$IyDE7bPCaGi.xXBRRJ000u3lh7rsc0tSnd8x2HBm8x38R51yPjqnm', 'USER', NOW(), NOW()),
('admin', 'admin@admin.ru', '$2a$10$/h.9xwJRU8hH3acszlUQAeJ/4kw4SNcQFywaacIK9xP5uOZWM1hri', 'ADMIN', NOW(), NOW());

-- Create tasks for users
-- We assume that 'user' got id=1, and 'admin' id=2
INSERT INTO tasks (title, description, status, user_id, created_at, updated_at) VALUES
('Wash the dishes', 'Very dirty, need detergent', 'PENDING', 1, NOW(), NOW()),
('Learn Spring Boot', 'Read the documentation and do a project', 'IN_PROGRESS', 1, NOW(), NOW()),
('Conquer the world', 'Start small, for example, with this project', 'PENDING', 2, NOW(), NOW());