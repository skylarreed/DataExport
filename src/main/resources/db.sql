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
    fraud BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (card_id) REFERENCES cards(id)

);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS cards (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    card_id INTEGER,
    card_number VARCHAR(20),
    expiration_date VARCHAR(10),
    cvv VARCHAR(10),
    card_type VARCHAR(10),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS merchants (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    merchant_name VARCHAR(50),
    merchant_city VARCHAR(50),
    merchant_state VARCHAR(30),
    merchant_zip VARCHAR(10),
    mcc INTEGER
);

CREATE TABLE IF NOT EXISTS states (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    state_name VARCHAR(50),
    state_abbr VARCHAR(2),
    state_capital VARCHAR(50),
    state_nickname VARCHAR(50)
);
