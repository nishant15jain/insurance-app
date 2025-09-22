-- Enhance payments table with additional fields for premium payments
ALTER TABLE payments 
ADD COLUMN payment_type ENUM('PREMIUM', 'CLAIM_SETTLEMENT', 'REFUND') NOT NULL DEFAULT 'PREMIUM',
ADD COLUMN due_date DATE NOT NULL,
ADD COLUMN payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'UPI', 'WALLET') DEFAULT 'CREDIT_CARD',
ADD COLUMN late_fee_amount DECIMAL(10,2) DEFAULT 0.00,
ADD COLUMN notes TEXT,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add indexes for efficient querying
CREATE INDEX idx_payments_due_date ON payments(due_date);
CREATE INDEX idx_payments_status_due ON payments(status, due_date);
CREATE INDEX idx_payments_user_policy_type ON payments(user_policy_id, payment_type);
