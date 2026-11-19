--liquibase formatted sql

--changeset caio.caminha:create-outbox-table
ALTER TABLE outbox
    ADD COLUMN ordering_key varchar(128) NOT NULL DEFAULT '';