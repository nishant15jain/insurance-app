-- Add premium_frequency column to policies table (only if it doesn't exist)
SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                      WHERE TABLE_SCHEMA = 'insuranceapp' 
                      AND TABLE_NAME = 'policies' 
                      AND COLUMN_NAME = 'premium_frequency');

SET @sql = IF(@column_exists = 0, 
              'ALTER TABLE policies ADD COLUMN premium_frequency VARCHAR(20) NOT NULL DEFAULT ''ANNUAL'' COMMENT ''Premium payment frequency: MONTHLY, QUARTERLY, HALF_YEARLY, ANNUAL''', 
              'SELECT ''Column premium_frequency already exists'' as message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing policies with default frequency
UPDATE policies SET premium_frequency = 'ANNUAL' WHERE premium_frequency IS NULL;
