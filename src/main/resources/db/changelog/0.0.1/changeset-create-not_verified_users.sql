--liquibase formatted sql
--changeset author:slava dbms:postgresql
CREATE TABLE IF NOT EXISTS not_verified_users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name  VARCHAR,
    email       VARCHAR,
    password    VARCHAR
);
--rollback DROP TABLE not_verified_users;