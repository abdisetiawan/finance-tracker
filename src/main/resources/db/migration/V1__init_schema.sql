CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100),
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE categories (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    type    VARCHAR(10) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE transactions (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount      NUMERIC(15,2) NOT NULL,
    type        VARCHAR(10) NOT NULL,
    note        TEXT,
    date        DATE NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO categories (name, type, user_id) VALUES
    ('Gaji', 'INCOME', NULL),
    ('Freelance', 'INCOME', NULL),
    ('Makan', 'EXPENSE', NULL),
    ('Transport', 'EXPENSE', NULL),
    ('Belanja', 'EXPENSE', NULL);