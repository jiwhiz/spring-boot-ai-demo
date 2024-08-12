CREATE TABLE demo.users
(
    id              BIGINT        NOT NULL,
    version         BIGINT        NOT NULL DEFAULT 1,
    email           VARCHAR(255)  NOT NULL UNIQUE,
    pw_hash         VARCHAR(100)  NOT NULL,
    first_name      VARCHAR(100)  NULL,
    last_name       VARCHAR(100)  NULL,
    image_url       VARCHAR(255)  NULL,
    activated       BOOL          NOT NULL,
    activation_key  VARCHAR(20)   NULL,
    reset_key       VARCHAR(20)   NULL,
    reset_date      TIMESTAMPTZ    NULL,
    created_at      TIMESTAMPTZ   NOT NULL,
    created_by      VARCHAR(100)  NOT NULL,
    updated_at      TIMESTAMPTZ   NOT NULL,
    updated_by      VARCHAR(100)  NOT NULL,
    PRIMARY KEY(id)
);

create index users_idx1
    on demo.users (email);

INSERT INTO demo.users(id, email, pw_hash, first_name, last_name, activated, created_at, created_by, updated_at, updated_by)
VALUES
 (1, 'yuan.ji@jiwhiz.com', '$2a$16$PQr94c9Z/blFDvAS4TRxBeV9zVsDOP16K8YXF4Pz6.UNTYDWMchuC', 'Administrator', 'Administrator', true, NOW(), 'system', NOW(), 'system')
;


CREATE TABLE demo.authority
(
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY (name)
);

create index authority_idx1
    on demo.authority (name);

INSERT INTO demo.authority(name)
VALUES ('ROLE_ADMIN'), ('ROLE_USER');


CREATE TABLE demo.user_authority
(
    user_id        BIGINT      NOT NULL,
    authority_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, authority_name)
);

INSERT INTO demo.user_authority(user_id, authority_name)
VALUES (1, 'ROLE_ADMIN'), (1, 'ROLE_USER');

