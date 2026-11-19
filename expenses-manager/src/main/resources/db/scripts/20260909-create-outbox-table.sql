--liquibase formatted sql

--changeset caio.caminha:create-outbox-table
CREATE TABLE IF NOT EXISTS outbox (
    id varchar(128) NOT NULL PRIMARY KEY,
    topic_name varchar(256) NOT NULL,
    payload text NOT NULL,
    sent_at timestamp,
    is_duplicate boolean default false,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL
);