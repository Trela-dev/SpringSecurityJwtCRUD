CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id) REFERENCES users(id)
);
