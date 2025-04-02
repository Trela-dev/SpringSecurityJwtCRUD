-- Insert default roles into the roles table
INSERT INTO roles (name) VALUES
('ROLE_USER'),    -- Default role for normal users
('ROLE_ADMIN'),   -- Role for administrators
('ROLE_MODERATOR'), -- Role for moderators
('ROLE_GUEST');   -- Role for guest users