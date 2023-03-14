

CREATE TABLE users (
    id INT NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE cards (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    card_id INT NOT NULL,
    card_number VARCHAR(255) NOT NULL,
    card_type VARCHAR(255) NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    expiration_date VARCHAR(7) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE merchants (
    id BIGINT NOT NULL PRIMARY KEY,
    merchant_name VARCHAR(255),
    merchant_city VARCHAR(255),
    merchant_state VARCHAR(255),
    merchant_zip VARCHAR(255),
    mcc INT NOT NULL
);

CREATE TABLE states (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(255),
    state_abbr VARCHAR(6),
    state_capital VARCHAR(255),
    state_nickname VARCHAR(255)
);

CREATE TABLE transactions (

    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    card_id INT NOT NULL,
    merchant_id BIGINT NOT NULL,
    "year" INT,
    "month" INT,
    "day" INT,
    "time" VARCHAR(255),
    "errors" VARCHAR(255),
    amount DECIMAL(10,2),
    type VARCHAR(255),
    is_fraud BOOLEAN,



    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);
