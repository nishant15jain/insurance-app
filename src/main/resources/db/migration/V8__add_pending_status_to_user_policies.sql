-- Add PENDING status to user_policies status enum
ALTER TABLE user_policies MODIFY COLUMN status ENUM('PENDING', 'ACTIVE', 'LAPSED', 'CANCELLED') NOT NULL DEFAULT 'PENDING';
