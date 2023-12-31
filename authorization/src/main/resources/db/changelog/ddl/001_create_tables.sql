-- liquibase formatted sql
-- changeset kate:20

CREATE TABLE role
(
    role_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(255),
    CONSTRAINT pk_role PRIMARY KEY (role_id)
);

CREATE TABLE users
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    talent_id  BIGINT,
    sponsor_id BIGINT,
    admin_id   BIGINT,
    role_id    BIGINT,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES role (role_id);

-- changeset kate:21
CREATE TABLE admin
(
    admin_id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    full_name VARCHAR(255),
    email     VARCHAR(255),
    password  VARCHAR(255),
    CONSTRAINT pk_admin PRIMARY KEY (admin_id)
);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ADMIN FOREIGN KEY (admin_id) REFERENCES admin (admin_id);