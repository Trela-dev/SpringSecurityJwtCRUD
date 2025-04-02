-- Insert admin user with bcrypt-hashed password (admin / admin)
INSERT INTO users (username, password, role_id)
VALUES (
    'admin',
    '$2a$10$OLM9qB4EzvRFweTXvO3KPOBJvEM9w44vRbDUhdJUeGCaN6RN0nTEG',
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
);

-- Insert moderator user with bcrypt-hashed password (moderator / moderator)
INSERT INTO users (username, password, role_id)
VALUES (
    'moderator',
    '$2a$10$OsqqIFz.BZSk2DO2rZsz2OiGZQJoG0OaMPd.mrtXgv2/b/R2UKh22',
    (SELECT id FROM roles WHERE name = 'ROLE_MODERATOR')
);