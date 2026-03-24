--liquibase formatted sql
--changeset author:slava dbms:postgresql
CREATE TABLE IF NOT EXISTS subscriptions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    visits_total    INT,
    snapshot_price  NUMERIC,
    purchase_at     DATE,
    is_active       BOOLEAN,
    visits_left     INT,
    tariff_type     VARCHAR,
    user_id         UUID,

    FOREIGN KEY (user_id) REFERENCES users(id)
);
--rollback DROP TABLE subscriptions;