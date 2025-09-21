CREATE TABLE claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_policy_id BIGINT NOT NULL,
    claim_amount DECIMAL(15,2) NOT NULL,
    claim_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'PAID') NOT NULL DEFAULT 'PENDING',
    description TEXT,
    processed_by BIGINT,
    FOREIGN KEY (user_policy_id) REFERENCES user_policies(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
);