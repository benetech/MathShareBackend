CREATE TABLE user_info (
    id SERIAL PRIMARY KEY,
    email VARCHAR (255) UNIQUE NOT NULL,
    user_type VARCHAR (255) NOT NULL,
    role VARCHAR (255) UNIQUE NOT NULL,
    grades TEXT DEFAULT '',
    date_created TIMESTAMP NOT NULL DEFAULT now()
);
