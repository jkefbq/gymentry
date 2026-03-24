--liquibase formatted sql
--changeset author:slava dbms:postgresql
CREATE TABLE IF NOT EXISTS users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name    VARCHAR,
    email         VARCHAR UNIQUE,
    password      VARCHAR,
    role          VARCHAR,
    total_visits  INT,
    member_since  DATE,
    last_visit    DATE,
    gym_address   VARCHAR,

    FOREIGN KEY (gym_address) REFERENCES gym_info(address)
);
--rollback DROP TABLE users;