CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    card_id INTEGER NOT NULL,
    year INTEGER,
    month INTEGER,
    day INTEGER,
    time VARCHAR(10),
    amount decimal(10,2),
    merchant_id INTEGER,
    merchant_state VARCHAR(2),
    merchant_city VARCHAR(50),
    merchant_zip VARCHAR(10),
    mcc INTEGER,
    errors VARCHAR(100),
    fraud BOOLEAN
);