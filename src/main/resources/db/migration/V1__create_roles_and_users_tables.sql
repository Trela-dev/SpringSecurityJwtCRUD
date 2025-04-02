
-- V1__create_roles_table.sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,      -- Auto-increment for the ID
    name VARCHAR(255) NOT NULL     -- Column for storing role name
);



-- V1__create_users_table.sql (Migration for creating the 'users' table)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    enabled BOOLEAN DEFAULT TRUE,
    role_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id) -- Załóżmy, że masz tabelę 'roles' z kolumną 'id'
);


