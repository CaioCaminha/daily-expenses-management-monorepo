--liquibase formatted sql

--changeset caio.caminha:20251106-create-user-details-table
CREATE TABLE IF NOT EXISTS user_details (
    id varchar(128) NOT NULL PRIMARY KEY,
    username varchar(256) NOT NULL,
    first_name varchar(128) NOT NULL,
    last_name varchar(128) NOT NULL,
    email varchar(128) NOT NULL UNIQUE,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL
);

--changeset caio.caminha:20251106-create-email-unique-index
CREATE UNIQUE INDEX IF NOT EXISTS UX_EMAIL_USER_DETAILS on user_details(email);

--changeset caio.caminha:20251106-create-transaction-details-table
--validCheckSum: 9:0907f8d3b6591ebdf232f8231ece5c7c
CREATE TABLE IF NOT EXISTS transaction_details (
    id varchar(128) NOT NULL PRIMARY KEY,
    user_id varchar(128) REFERENCES user_details(id),
    details varchar(250) NOT NULL,
    category varchar(128) NOT NULL,
    cost NUMERIC NOT NULL,
    date_executed DATE NOT NULL,
    paid_by varchar(128), --might add references to user_details email for cases when the someone paid for you with your money
    created_at timestamp not null default NOW()::timestamp,
    updated_at timestamp not null default NOW()::timestamp
);

--changeset caio.caminha:20251106-create-index-user-id-transaction-details
CREATE INDEX IF NOT EXISTS IX_USER_ID_TRANSACTION_DETAILS on transaction_details(user_id);

