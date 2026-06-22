CREATE TABLE budgets (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(id) ON DELETE CASCADE,
    amount      DECIMAL NOT NULL,
    month       INTEGER NOT NULL,
    year        INTEGER NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),

    UNIQUE(user_id, category_id, month, year)
);