--liquibase formatted sql
--changeset author:slava dbms:postgresql
CREATE TABLE IF NOT EXISTS visits (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at       TIMESTAMP,
    gym_address      VARCHAR,
    subscription_id  UUID,

    FOREIGN KEY (gym_address) REFERENCES gym_info(address),
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);
--rollback DROP TABLE visits;