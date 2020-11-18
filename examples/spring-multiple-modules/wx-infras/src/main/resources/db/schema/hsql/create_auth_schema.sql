CREATE TABLE auth_access_key
(
    tenant_id   BIGINT       NOT NULL,
    key         VARCHAR(32)  NOT NULL,
    secret      VARCHAR(64)  NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id   BIGINT       NOT NULL,
    CONSTRAINT uniq_auth_access_key UNIQUE (key)
);
