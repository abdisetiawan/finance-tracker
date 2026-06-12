-- Index untuk filter yang paling sering dipakai
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_date ON transactions(date);

-- Composite index untuk filter kombinasi user + date (paling sering)
CREATE INDEX idx_transactions_user_date ON transactions(user_id, date);