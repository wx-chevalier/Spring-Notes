-- u_user
-- u_role
-- u_permission
-- u_tenant
-- u_company
-- u_user_credentials
--
-- u_user_role_relation
-- u_user_permission_relation
-- u_role_permission_relation
-- u_user_tenant_relation

CREATE TABLE u_user
(
    id             BIGINT AUTO_INCREMENT,
    tenant_id      BIGINT                                                             NOT NULL,
    deleted_at     TIMESTAMP    DEFAULT NULL,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    username       VARCHAR(32)                                                        NOT NULL,
    phone_number   VARCHAR(32),
    nick_name      VARCHAR(64)                                                        NOT NULL,
    email          VARCHAR(128),
    avatar_file_id BIGINT       DEFAULT NULL,
    remark         VARCHAR(128) DEFAULT NULL,
    creator_id     BIGINT                                                             NOT NULL,
    authority      VARCHAR(16)  DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uniq_u_user_username UNIQUE (tenant_id, username),
    CONSTRAINT uniq_u_user_phone_number UNIQUE (tenant_id, phone_number),
    CONSTRAINT uniq_u_user_email UNIQUE (tenant_id, email)
);
CREATE TABLE u_role
(
    id           BIGINT AUTO_INCREMENT,
    tenant_id    BIGINT                                                          NOT NULL,
    deleted_at   TIMESTAMP DEFAULT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name         VARCHAR(128)                                                    NOT NULL,
    description  VARCHAR(512),
    nickname     VARCHAR(100),
    is_disabled  TINYINT   DEFAULT 0,
    creator_id   BIGINT                                                          NOT NULL,
    icon_file_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uniq_u_role_name UNIQUE (tenant_id, name),
    CONSTRAINT uniq_u_role_nickname UNIQUE (tenant_id, nickname)
);
CREATE TABLE u_permission
(
    id           BIGINT AUTO_INCREMENT,
    tenant_id    BIGINT                                                          NOT NULL,
    deleted_at   TIMESTAMP DEFAULT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name         VARCHAR(128)                                                    NOT NULL,
    description  VARCHAR(512),
    nickname     VARCHAR(100),
    is_disabled  TINYINT   DEFAULT 0,
    creator_id   BIGINT                                                          NOT NULL,
    icon_file_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uniq_u_permission_name UNIQUE (tenant_id, name),
    CONSTRAINT uniq_u_permission_nickname UNIQUE (tenant_id, nickname)
);
CREATE TABLE u_user_permission_relation
(
    tenant_id     BIGINT                                                          NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    user_id       BIGINT                                                          NOT NULL,
    permission_id BIGINT                                                          NOT NULL,
    is_disabled   TINYINT   DEFAULT 0,
    creator_id    BIGINT                                                          NOT NULL,
    CONSTRAINT uniq_u_user_permission_relation UNIQUE (tenant_id, user_id, permission_id)
);
CREATE TABLE u_user_role_relation
(
    tenant_id   BIGINT                                                          NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    user_id     BIGINT                                                          NOT NULL,
    role_id     BIGINT                                                          NOT NULL,
    is_disabled TINYINT   DEFAULT 0,
    creator_id  BIGINT                                                          NOT NULL,
    CONSTRAINT uniq_u_user_role_relation UNIQUE (tenant_id, user_id, role_id)
);
CREATE TABLE u_role_permission_relation
(
    tenant_id     BIGINT                                                          NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    permission_id BIGINT                                                          NOT NULL,
    role_id       BIGINT                                                          NOT NULL,
    is_disabled   BIGINT                                                          NOT NULL,
    creator_id    BIGINT                                                          NOT NULL,
    CONSTRAINT uniq_u_role_permission_relation UNIQUE (tenant_id, role_id, permission_id)
);
CREATE TABLE u_user_credentials
(
    tenant_id      BIGINT                                                          NOT NULL,
    user_id        BIGINT                                                          NOT NULL,
    deleted_at     TIMESTAMP DEFAULT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    is_enabled     TINYINT   DEFAULT 1,
    activate_token VARCHAR(255),
    password       VARCHAR(255),
    reset_token    VARCHAR(255),
    CONSTRAINT uniq_u_user_credentials UNIQUE (tenant_id, user_id),
    CONSTRAINT uniq_u_user_credentials_activate_token UNIQUE (activate_token),
    CONSTRAINT uniq_u_user_credentials_reset_token UNIQUE (reset_token)
);
CREATE TABLE u_tenant
(
    id         BIGINT AUTO_INCREMENT,
    tenant_id  BIGINT    DEFAULT 0                                             NOT NULL,
    deleted_at TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name       VARCHAR(128)                                                    NOT NULL,
    company_id BIGINT                                                          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uniq_u_tenant_name UNIQUE (tenant_id, name)
);
CREATE TABLE u_user_tenant_relation
(
    tenant_id  BIGINT                                                          NOT NULL,
    user_id    BIGINT                                                          NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uniq_u_user_tenant_relation UNIQUE (tenant_id, user_id)
);
CREATE TABLE u_company
(
    id         BIGINT AUTO_INCREMENT,
    deleted_at TIMESTAMP DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name       VARCHAR(255),
    area_code  VARCHAR(32),
    PRIMARY KEY (id),
    CONSTRAINT uniq_u_company_name UNIQUE (name)
);